package org.example.bronze.connector.target;

import org.example.bronze.metadata.FileMetadata;
import org.example.bronze.metadata.VersionStore;

import java.nio.file.Path;

public interface ProcessEngineInterface
{
    public void process(FileMetadata meta, VersionStore store, Path newFilePath) throws Exception;
}

