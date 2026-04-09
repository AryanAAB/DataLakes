package org.example.bronze.ingestion.pipeline;

import org.example.bronze.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineManager
{
    private final Map<Long, IngestionPipeline> pipelines;

    public PipelineManager()
    {
        pipelines = new HashMap<>();
    }

    public void registerPipeline(IngestionPipeline pipeline)
    {
        if (pipeline == null || pipelines.containsKey(pipeline.getId()))
        {
            Constants.logger.warn("Pipeline {} already exists or is null", pipeline == null ? "null" : pipeline.getId());
            return;
        }

        pipelines.put(pipeline.getId(), pipeline);
    }

    public void registerPipeline(List<IngestionPipeline> pipelines)
    {
        if (pipelines == null) return;

        pipelines.forEach(this::registerPipeline);
    }

    public void triggerNow(Long id, int numThreads) throws Exception
    {
        IngestionPipeline pipeline = pipelines.get(id);
        if (pipeline == null) throw new Exception("Pipeline not found");

        pipeline.run(numThreads);
    }
}
