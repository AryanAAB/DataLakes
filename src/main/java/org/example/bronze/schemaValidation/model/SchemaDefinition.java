package org.example.bronze.schemaValidation.model;

import java.nio.file.Path;

public record SchemaDefinition(
        String id,
        SchemaType type,
        Path location,
        Path customValidatorPath
)
{
}
