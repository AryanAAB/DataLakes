package org.example.bronze;

import org.example.bronze.ingestion.pipeline.IngestionPipeline;
import org.example.bronze.ingestion.pipeline.PipelineManager;
import org.example.bronze.ingestion.pipeline.PipelineRepository;

import java.util.List;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        List<IngestionPipeline> ingestion = PipelineRepository.loadActivePipelines();

        PipelineManager pipelineManager = new PipelineManager();
        pipelineManager.registerPipeline(ingestion);

        for (IngestionPipeline pipeline : ingestion)
            pipelineManager.triggerNow(pipeline.getId(), 5);
    }
}
