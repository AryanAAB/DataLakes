package org.example.bronze.connector.source;

import org.example.bronze.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class LocalSourceConnector implements SourceConnector
{
    private final Path sourceDirectory;
    private final Path dumpDirectory;

    public LocalSourceConnector(Path sourceDirectory, Path dumpDirectory)
    {
        this.sourceDirectory = sourceDirectory;
        this.dumpDirectory = dumpDirectory;

        if (!Files.isDirectory(this.sourceDirectory))
        {
            throw new IllegalArgumentException("Path is not a directory: " + sourceDirectory);
        }
        else if (!Files.isDirectory(this.dumpDirectory))
        {
            throw new IllegalArgumentException("Path is not a directory: " + dumpDirectory);
        }
    }

    @Override
    public FileTransferResult readAndDump() throws IOException
    {
        List<FileFailure> failures = new ArrayList<>();
        AtomicLong totalFiles = new AtomicLong();
        AtomicLong successFiles = new AtomicLong();

        try (Stream<Path> paths = Files.walk(this.sourceDirectory))
        {
            paths.forEach(sourcePath ->
            {
                Path relativePath = this.sourceDirectory.relativize(sourcePath);

                //resolve target path inside dump directory
                Path targetPath = this.dumpDirectory.resolve(relativePath);

                try
                {
                    if (Files.isDirectory(sourcePath))
                    {
                        Files.createDirectories(targetPath);
                    }
                    else
                    {
                        totalFiles.getAndIncrement();
                        Files.createDirectories(targetPath.getParent());
                        Files.copy(
                                sourcePath,
                                targetPath,
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES
                        );
                        successFiles.getAndIncrement();
                    }
                } catch (IOException e)
                {
                    failures.add(new FileFailure(
                            sourcePath,
                            targetPath,
                            Files.isDirectory(sourcePath) ? OperationType.CREATE_DIR : OperationType.COPY,
                            e.getMessage(),
                            e
                    ));

                    String op = Files.isDirectory(sourcePath) ? "create directory" : "copy file";
                    Constants.logger.error("Failed to " + op + ": " + sourcePath, e);
                } catch (Exception e)
                {
                    failures.add(new FileFailure(
                            sourcePath,
                            targetPath,
                            OperationType.UNKNOWN,
                            e.getMessage(),
                            e
                    ));

                    Constants.logger.error("Failed to create/copy file: " + sourcePath, e);
                }
            });
        } catch (IOException e)
        {
            Constants.logger.error("Failed to read source directory: " + sourceDirectory, e);
            throw new IOException(e);
        }

        return new FileTransferResult(totalFiles.get(), successFiles.get(), failures);
    }
}