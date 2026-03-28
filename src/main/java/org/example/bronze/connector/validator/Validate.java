package org.example.bronze.connector.validator;

import org.example.bronze.metadata.FileMetadata;

public interface Validate
{
    boolean isValid(FileMetadata fileMetadata) throws Exception;
}
