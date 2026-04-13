package org.example.bronze.schemaValidation.service;

import org.example.bronze.schemaValidation.db.MappingRepository;
import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.util.ValidationConstants;
import org.example.bronze.schemaValidation.validator.SchemaValidator;
import org.example.bronze.schemaValidation.validator.ValidationResult;
import org.example.bronze.schemaValidation.validator.ValidatorFactory;
import org.example.bronze.util.Constants;
import org.example.bronze.util.DatabaseConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public final class ValidationPipeline
{
    private final List<SchemaDefinition> schemaDefinitions;
    private final ValidatorFactory validatorFactory;
    private final MappingRepository mappingRepository;
    private final SchemaSelectionStrategy schemaSelectionStrategy;

    public ValidationPipeline(
            List<SchemaDefinition> schemaDefinitions,
            ValidatorFactory validatorFactory,
            MappingRepository mappingRepository
    )
    {
        this(schemaDefinitions, validatorFactory, mappingRepository, new SchemaSelectionStrategy());
    }

    ValidationPipeline(
            List<SchemaDefinition> schemaDefinitions,
            ValidatorFactory validatorFactory,
            MappingRepository mappingRepository,
            SchemaSelectionStrategy schemaSelectionStrategy
    )
    {
        this.schemaDefinitions = List.copyOf(schemaDefinitions);
        this.validatorFactory = validatorFactory;
        this.mappingRepository = mappingRepository;
        this.schemaSelectionStrategy = schemaSelectionStrategy;
    }

    public void run() throws Exception
    {
        process();
    }

    private void process() throws Exception
    {
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(ValidationConstants.GET_UNACCEPTED_SQL);
             ResultSet rs = stmt.executeQuery())
        {
            while (rs.next())
            {
                String pathStr = rs.getString("path");

                if (pathStr == null) continue;

                Path file = Paths.get(pathStr);

                if (Files.isRegularFile(file))
                {
                    processFileSafely(file);
                }
                else
                {
                    // optional: log missing or invalid file
                }
            }
        }
    }

    private void processFileSafely(Path file)
    {
        try
        {
            processFile(file);
        } catch (Exception exception)
        {
            Constants.logger.error("Failed to process {}: {}", file.getFileName(), exception.getMessage());
        }
    }

    private void processFile(Path file) throws Exception
    {
        Constants.logger.info("Validating {}", file.getFileName());

        for (SchemaDefinition schemaDefinition : schemaSelectionStrategy.orderCandidates(file, schemaDefinitions))
        {
            SchemaValidator validator = validatorFactory.getValidator(schemaDefinition);
            ValidationResult result = validator.validate(file, schemaDefinition);

            if (result.matched())
            {
                persistMappingSafely(file, schemaDefinition);
                Constants.logger.info(
                        "Accepted {} with schema {}",
                        file.getFileName(),
                        schemaDefinition.id()
                );
                return;
            }

            Constants.logger.error(
                    "Schema {} did not match {}: {}",
                    schemaDefinition.id(),
                    file.getFileName(),
                    result.message()
            );
        }

        Constants.logger.error("Rejected {} because no schema matched", file.getFileName());
    }

    private void persistMappingSafely(Path acceptedFile, SchemaDefinition schemaDefinition) throws Exception
    {
        try
        {
            mappingRepository.insertMapping(acceptedFile.toAbsolutePath(), schemaDefinition);
        } catch (Exception exception)
        {
            Constants.logger.error(
                    "Accepted {} but failed to write mapping row for schema {}: {}",
                    acceptedFile.getFileName(),
                    schemaDefinition.id(),
                    exception.getMessage()
            );
            throw exception;
        }
    }
}
