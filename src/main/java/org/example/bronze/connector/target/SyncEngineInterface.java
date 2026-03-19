package org.example.bronze.connector.target;

import org.example.bronze.metadata.FileMetadata;

public interface SyncEngineInterface
{
    void public sync() throws Exception;
    void private process(FileMetadata meta) throws Exception;
}
