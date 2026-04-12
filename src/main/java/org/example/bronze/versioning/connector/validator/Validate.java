package org.example.bronze.versioning.connector.validator;

import org.example.bronze.versioning.metadata.FileMetadata;

public interface Validate
{
    boolean isValid(FileMetadata fileMetadata) throws Exception;
}
