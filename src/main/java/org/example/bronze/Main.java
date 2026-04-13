package org.example.bronze;

import org.example.bronze.ingestion.pipeline.IngestionPipeline;
import org.example.bronze.ingestion.pipeline.PipelineManager;
import org.example.bronze.ingestion.pipeline.PipelineRepository;
import org.example.bronze.schemaValidation.db.FileIdProvider;
import org.example.bronze.schemaValidation.db.MappingRepository;
import org.example.bronze.schemaValidation.db.SchemaRepository;
import org.example.bronze.schemaValidation.service.ValidationPipeline;
import org.example.bronze.schemaValidation.validator.ValidatorFactory;
import org.example.bronze.util.Constants;
import org.example.bronze.util.DatabaseConfig;

import java.sql.Connection;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        try
        {
            List<IngestionPipeline> ingestion = PipelineRepository.loadActivePipelines();

            PipelineManager pipelineManager = new PipelineManager();
            pipelineManager.registerPipeline(ingestion);

            for (IngestionPipeline pipeline : ingestion)
                pipelineManager.triggerNow(pipeline.getId(), 5);
        } catch (Exception e)
        {
            Constants.logger.error(e.getMessage());
        }

        try
        {
            ValidatorFactory validatorFactory = new ValidatorFactory();
            Connection connection = DatabaseConfig.getDataSource().getConnection();

            SchemaRepository schemaRepository = new SchemaRepository(connection);
            MappingRepository mappingRepository = new MappingRepository(connection, new FileIdProvider());

            var schemaDefinitions = schemaRepository.findAll();

            ValidationPipeline pipeline = new ValidationPipeline(
                    schemaDefinitions,
                    validatorFactory,
                    mappingRepository
            );

            pipeline.run();
        } catch (Exception exception)
        {
            Constants.logger.error(exception.getMessage());
        }
    }
}
