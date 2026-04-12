package org.example.bronze.schemaValidation.service;

import org.example.bronze.schemaValidation.db.MappingRepository;
import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.validator.SchemaValidator;
import org.example.bronze.schemaValidation.validator.ValidationResult;
import org.example.bronze.schemaValidation.validator.ValidatorFactory;
import org.example.bronze.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class ValidationPipeline
{
    private final List<SchemaDefinition> schemaDefinitions;
    private final ValidatorFactory validatorFactory;
    private final FileRouter fileRouter;
    private final MappingRepository mappingRepository;
    private final SchemaSelectionStrategy schemaSelectionStrategy;

    public ValidationPipeline(
            List<SchemaDefinition> schemaDefinitions,
            ValidatorFactory validatorFactory,
            FileRouter fileRouter,
            MappingRepository mappingRepository
    )
    {
        this(schemaDefinitions, validatorFactory, fileRouter, mappingRepository, new SchemaSelectionStrategy());
    }

    ValidationPipeline(
            List<SchemaDefinition> schemaDefinitions,
            ValidatorFactory validatorFactory,
            FileRouter fileRouter,
            MappingRepository mappingRepository,
            SchemaSelectionStrategy schemaSelectionStrategy
    )
    {
        this.schemaDefinitions = List.copyOf(schemaDefinitions);
        this.validatorFactory = validatorFactory;
        this.fileRouter = fileRouter;
        this.mappingRepository = mappingRepository;
        this.schemaSelectionStrategy = schemaSelectionStrategy;
    }

    public void run() throws Exception
    {
        try (Stream<Path> stream = Files.walk(Paths.get(Constants.PIPELINE_STAGING_DIRECTORY)))
        {
            stream.filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .forEach(this::processFileSafely);
        }
    }

    private void processFileSafely(Path file)
    {
        try
        {
            processFile(file);
        } catch (Exception exception)
        {
            System.out.printf("Failed to process %s: %s%n", file.getFileName(), exception.getMessage());
            try
            {
                fileRouter.routeRejected(file);
            } catch (IOException moveException)
            {
                throw new RuntimeException("Unable to move failed file to rejected folder: " + file, moveException);
            }
        }
    }

    private void processFile(Path file) throws Exception
    {
        System.out.printf("Validating %s%n", file.getFileName());

        for (SchemaDefinition schemaDefinition : schemaSelectionStrategy.orderCandidates(file, schemaDefinitions))
        {
            SchemaValidator validator = validatorFactory.getValidator(schemaDefinition);
            ValidationResult result = validator.validate(file, schemaDefinition);

            if (result.matched())
            {
                Path acceptedFile = fileRouter.routeAccepted(file);
                persistMappingSafely(acceptedFile, schemaDefinition);
                System.out.printf(
                        "Accepted %s with schema %s%n",
                        acceptedFile.getFileName(),
                        schemaDefinition.id()
                );
                return;
            }

            System.out.printf(
                    "Schema %s did not match %s: %s%n",
                    schemaDefinition.id(),
                    file.getFileName(),
                    result.message()
            );
        }

        fileRouter.routeRejected(file);
        System.out.printf("Rejected %s because no schema matched%n", file.getFileName());
    }

    private void persistMappingSafely(Path acceptedFile, SchemaDefinition schemaDefinition)
    {
        try
        {
            mappingRepository.insertMapping(acceptedFile, schemaDefinition);
        } catch (Exception exception)
        {
            System.out.printf(
                    "Accepted %s but failed to write mapping row for schema %s: %s%n",
                    acceptedFile.getFileName(),
                    schemaDefinition.id(),
                    exception.getMessage()
            );
        }
    }
}
