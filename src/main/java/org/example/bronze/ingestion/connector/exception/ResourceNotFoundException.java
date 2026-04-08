package org.example.bronze.ingestion.connector.exception;

public class ResourceNotFoundException extends SourceConnectorException
{
    public ResourceNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
