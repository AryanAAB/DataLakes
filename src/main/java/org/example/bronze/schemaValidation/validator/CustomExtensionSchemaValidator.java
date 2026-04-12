package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;

import java.nio.file.Path;

public final class CustomExtensionSchemaValidator implements SchemaValidator
{
    @Override
    public ValidationResult validate(Path dataFile, SchemaDefinition schemaDefinition)
    {
        String dataExtension = extensionOf(dataFile);
        String schemaExtension = extensionOf(schemaDefinition.location());

        if (schemaExtension.equals(dataExtension))
        {
            String descriptor = schemaExtension.isEmpty() ? "[no extension]" : "." + schemaExtension;
            return ValidationResult.matched("Matched by fallback extension rule: " + descriptor);
        }

        String dataDescriptor = dataExtension.isEmpty() ? "[no extension]" : "." + dataExtension;
        String schemaDescriptor = schemaExtension.isEmpty() ? "[no extension]" : "." + schemaExtension;
        return ValidationResult.notMatched(
                "Fallback extension rule did not match: data file uses %s but schema uses %s"
                        .formatted(dataDescriptor, schemaDescriptor)
        );
    }

    private String extensionOf(Path path)
    {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1)
        {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
