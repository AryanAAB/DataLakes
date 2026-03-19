package org.example.bronze.connector.target;

public interface SyncEngineInterface
{
    void public sync() throws Exception;
    void private process(FileMetadata meta) throws Exception;
}
