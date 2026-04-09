package org.example.bronze.ingestion.pipeline;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.example.bronze.ingestion.connector.SourceConnector;
import org.example.bronze.ingestion.metadata.FileMetadata;
import org.example.bronze.util.Constants;
import org.example.bronze.util.DatabaseConfig;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocalIngestionPipeline implements IngestionPipeline
{
    private final SourceConnector sourceConnector;
    private final String name;
    private final long pipelineId;
    private final Path outputDirectory;

    private static final MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
    private static final Tika tika = new Tika();

    public LocalIngestionPipeline(SourceConnector sourceConnector, Path outputDirectory, String name, long pipelineId)
    {
        this.sourceConnector = sourceConnector;
        this.name = name;
        this.pipelineId = pipelineId;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public synchronized void run(int numThreads) throws Exception
    {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try
        {
            sourceConnector.connect();

            Constants.logger.info("Loading resources from source for pipeline {}", pipelineId);

            List<FileMetadata> resources = sourceConnector.listResources();

            Constants.logger.info("Resources loaded from source for pipeline {}", pipelineId);

            List<Future<?>> futures = new ArrayList<>();

            resources.forEach(resource ->
                    futures.add(executor.submit(() ->
                    {
                        try (InputStream in = sourceConnector.fetchResource(resource))
                        {
                            if (in != null)
                            {
                                String extension = allTypes.forName(resource.getExportMimeType()).getExtension();
                                try
                                {
                                    String detection = tika.detect(resource.getName());
                                    String detectedExtension = detection == null ? null : allTypes.forName(detection).getExtension();

                                    if (detectedExtension == null || !detectedExtension.equals(extension))
                                        resource.setName(resource.getName() + extension);
                                } catch (MimeTypeException e)
                                {
                                    Constants.logger.error(e.getMessage(), e);
                                }

                                Path target = outputDirectory.resolve(resource.getName());
                                Files.createDirectories(target.getParent());
                                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (Exception e)
                        {
                            throw new RuntimeException("Failed for resource: " + resource, e);
                        }

                        addResource(resource);
                        Constants.logger.info("Resource {} with name {} extracted from source for pipeline {}",
                                resource.getId(), resource.getName(), pipelineId);
                    })));

            for (Future<?> future : futures)
            {
                try
                {
                    future.get();
                } catch (ExecutionException e)
                {
                    Constants.logger.error(e.getMessage(), e);
                }
            }
        } finally
        {
            executor.shutdown();
            sourceConnector.close();
        }
    }

    private void addResource(FileMetadata resource)
    {
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.ADD_METADATA))
        {
            ps.setLong(1, getId());
            ps.setString(2, resource.getId());
            ps.setString(3, resource.getParentId());
            ps.setString(4, resource.getName());
            ps.setString(5, resource.getMimeType());
            ps.setString(6, resource.getExportMimeType());
            ps.setObject(7, resource.getSize());
            ps.setTimestamp(8, Timestamp.from(resource.getCreatedTime()));
            ps.setObject(9, Timestamp.from(resource.getModifiedTime()));

            ps.executeUpdate();
        } catch (SQLException e)
        {
            throw new RuntimeException("Failed to insert FileMetadata: " + resource, e);
        }
    }

    @Override
    public Long getId()
    {
        return pipelineId;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public Path getOutputDirectory()
    {
        return outputDirectory;
    }
}
