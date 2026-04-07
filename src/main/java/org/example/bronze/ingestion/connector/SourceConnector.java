package org.example.bronze.ingestion.connector;

import org.example.bronze.ingestion.connector.exception.ResourceNotFoundException;
import org.example.bronze.ingestion.connector.exception.SourceConnectorException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SourceConnector extends AutoCloseable
{
    void connect() throws SourceConnectorException;

    @Override
    void close() throws SourceConnectorException;

    boolean isConnected();

    List<String> listResources() throws SourceConnectorException;

    default List<String> listResources(Map<String, Object> filters) throws SourceConnectorException
    {
        return listResources();
    }

    InputStream fetchResource(String resourceId) throws ResourceNotFoundException;
}