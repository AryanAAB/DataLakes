package org.example.bronze.connector.target;

import org.example.bronze.metadata.FileMetadata;

public interface ProcessEngineInterface
{
    public void process(FileMetadata meta) throws Exception;
}

