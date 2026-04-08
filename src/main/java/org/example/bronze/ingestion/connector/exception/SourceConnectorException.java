package org.example.bronze.ingestion.connector.exception;

public class SourceConnectorException extends Exception
{
    public SourceConnectorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
