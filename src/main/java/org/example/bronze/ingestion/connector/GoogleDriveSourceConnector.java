package org.example.bronze.ingestion.connector;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.example.bronze.ingestion.connector.exception.ResourceNotFoundException;
import org.example.bronze.ingestion.connector.exception.SourceConnectorException;
import org.example.bronze.ingestion.metadata.FileMetadata;
import org.example.bronze.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GoogleDriveSourceConnector implements SourceConnector
{
    private final Drive driveClient;

    public GoogleDriveSourceConnector(Drive driveClient)
    {
        this.driveClient = driveClient;
    }

    @Override
    public void connect()
    {
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean isConnected()
    {
        return true;
    }

    @Override
    public List<FileMetadata> listResources() throws SourceConnectorException
    {
        try
        {
            FileList result = driveClient.files()
                    .list()
                    .setFields("files(id, name, mimeType, size, createdTime, modifiedTime, parents)")
                    .execute();

            List<FileMetadata> metadata = new ArrayList<>();
            for (File file : result.getFiles())
            {
                metadata.add(new FileMetadata()
                        .setId(file.getId())
                        .setParentId(file.getParents() == null ? null : file.getParents().getFirst())
                        .setName(file.getName())
                        .setMimeType(file.getMimeType())
                        .setSize(file.getSize())
                        .setCreatedTime(Instant.parse(file.getCreatedTime().toStringRfc3339()))
                        .setModifiedTime(Instant.parse(file.getModifiedTime().toStringRfc3339()))
                );
            }
            return metadata;
        } catch (Exception e)
        {
            Constants.logger.error(e.getMessage(), e);
            throw new SourceConnectorException("Failed to list resources", e);
        }
    }

    @Override
    public InputStream fetchResource(FileMetadata metadata) throws SourceConnectorException
    {
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (metadata.getMimeType().startsWith("application/vnd.google-apps.folder"))
            {
                return null;
            }
            if (metadata.getMimeType().startsWith("application/vnd.google-apps"))
            {
                metadata.setExportMimeType("application/pdf");
                // Export Google Docs as PDF
                driveClient.files().export(metadata.getId(), "application/pdf")
                        .executeMediaAndDownloadTo(out);
            }
            else
            {
                metadata.setExportMimeType(metadata.getMimeType());
                // Normal binary file
                driveClient.files().get(metadata.getId())
                        .executeMediaAndDownloadTo(out);
            }
            return new java.io.ByteArrayInputStream(out.toByteArray());

        } catch (GoogleJsonResponseException e)
        {
            Constants.logger.error(e.getMessage(), e);
            if (e.getStatusCode() == 404)
            {
                throw new ResourceNotFoundException("Resource not found: " + metadata.getId(), e);
            }
            throw new SourceConnectorException("Failed to fetch resource: " + metadata.getId(), e);
        } catch (Exception e)
        {
            Constants.logger.error(e.getMessage(), e);
            throw new SourceConnectorException("Failed to fetch resource: " + metadata.getId(), e);
        }
    }
}