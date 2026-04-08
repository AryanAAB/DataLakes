package org.example.bronze.ingestion.pipeline;

import java.util.HashMap;
import java.util.Map;

public class PipelineManager
{
    private final Map<Long, IngestionPipeline> pipelines;

    public PipelineManager()
    {
        pipelines = new HashMap<>();
    }

    public boolean registerPipeline(IngestionPipeline pipeline)
    {
        if (pipeline == null) return false;
        if (pipelines.containsKey(pipeline.getId())) return true;

        return pipelines.put(pipeline.getId(), pipeline) == null;
    }

    public void triggerNow(Long id, int numThreads) throws Exception
    {
        IngestionPipeline pipeline = pipelines.get(id);
        if (pipeline == null) throw new Exception("Pipeline not found");

        pipeline.run(numThreads);
    }
}
