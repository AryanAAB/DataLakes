package org.example.bronze.ingestion.pipeline;

public interface PipelineManager
{
    boolean registerPipeline(IngestionPipeline pipeline) throws Exception;

    void triggerNow() throws Exception;
}
