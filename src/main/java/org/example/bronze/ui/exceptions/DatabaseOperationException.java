package org.example.bronze.ui.exceptions;

public class DatabaseOperationException extends Exception
{
    public DatabaseOperationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}