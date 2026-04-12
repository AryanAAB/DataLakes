package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;

import java.nio.file.Path;

public interface SchemaValidator
{
    ValidationResult validate(Path dataFile, SchemaDefinition schemaDefinition) throws Exception;
}
