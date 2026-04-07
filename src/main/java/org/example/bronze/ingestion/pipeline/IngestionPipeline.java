package org.example.bronze.ingestion.pipeline;

public interface IngestionPipeline
{
    void run() throws Exception;

    String getName();
}
