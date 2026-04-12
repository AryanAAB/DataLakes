package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;

import java.nio.file.Path;

public final class UnsupportedSchemaValidator implements SchemaValidator
{
    @Override
    public ValidationResult validate(Path dataFile, SchemaDefinition schemaDefinition)
    {
        return ValidationResult.notMatched(
                "Validator type %s is reserved for future extensibility".formatted(schemaDefinition.type())
        );
    }
}
