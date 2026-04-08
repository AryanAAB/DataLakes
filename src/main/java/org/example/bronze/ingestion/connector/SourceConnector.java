package org.example.bronze.ingestion.connector;

import org.example.bronze.ingestion.connector.exception.ResourceNotFoundException;
import org.example.bronze.ingestion.connector.exception.SourceConnectorException;
import org.example.bronze.ingestion.metadata.FileMetadata;

import java.io.InputStream;
import java.util.List;

public interface SourceConnector
{
    void connect() throws SourceConnectorException;

    void close() throws SourceConnectorException;

    boolean isConnected();

    List<FileMetadata> listResources() throws SourceConnectorException;

    InputStream fetchResource(FileMetadata resource) throws SourceConnectorException, ResourceNotFoundException;
}