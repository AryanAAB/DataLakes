package org.example.bronze.schemaValidation;

import org.example.bronze.schemaValidation.db.FileIdProvider;
import org.example.bronze.schemaValidation.db.MappingRepository;
import org.example.bronze.schemaValidation.db.SchemaRepository;
import org.example.bronze.schemaValidation.service.FileRouter;
import org.example.bronze.schemaValidation.service.ValidationPipeline;
import org.example.bronze.schemaValidation.validator.ValidatorFactory;
import org.example.bronze.util.Constants;
import org.example.bronze.util.DatabaseConfig;

import java.nio.file.Path;
import java.sql.Connection;

public final class Application
{

    private Application()
    {
    }

    public static void main(String[] args)
    {
        try
        {
            ValidatorFactory validatorFactory = new ValidatorFactory();
            Connection connection = DatabaseConfig.getDataSource().getConnection();

            SchemaRepository schemaRepository = new SchemaRepository(connection);
            MappingRepository mappingRepository = new MappingRepository(connection, new FileIdProvider());
            FileRouter router = new FileRouter(Path.of(Constants.VALIDATOR_ACCEPTED_DIRECTORY), Path.of(Constants.VALIDATOR_REJECTED_DIRECTORY));

            var schemaDefinitions = schemaRepository.findAll();

            ValidationPipeline pipeline = new ValidationPipeline(
                    schemaDefinitions,
                    validatorFactory,
                    router,
                    mappingRepository
            );

            pipeline.run();
        } catch (Exception exception)
        {
            System.out.println("Pipeline execution failed: " + exception.getMessage());
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
