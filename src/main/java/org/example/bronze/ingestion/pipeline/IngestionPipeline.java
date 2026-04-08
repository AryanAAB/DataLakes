package org.example.bronze.ingestion.pipeline;

public interface IngestionPipeline
{
    void run(int numThreads) throws Exception;

    Long getId();

    String getName();
}
