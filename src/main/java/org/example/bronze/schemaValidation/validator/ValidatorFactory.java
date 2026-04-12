package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.model.SchemaType;

import java.util.EnumMap;
import java.util.Map;

public final class ValidatorFactory
{
    private final Map<SchemaType, SchemaValidator> validators;
    private final SchemaValidator customExecutableValidator;
    private final SchemaValidator customExtensionFallbackValidator;

    public ValidatorFactory()
    {
        validators = new EnumMap<>(SchemaType.class);
        customExecutableValidator = new CustomExecutableSchemaValidator();
        customExtensionFallbackValidator = new CustomExtensionSchemaValidator();
        validators.put(SchemaType.XSD, new XsdSchemaValidator());
        validators.put(SchemaType.SQL_DDL, new SqlDdlSchemaValidator());
        validators.put(SchemaType.CSV, new CsvSchemaValidator());
        validators.put(SchemaType.XLSX, new XlsxSchemaValidator());
    }

    public SchemaValidator getValidator(SchemaDefinition schemaDefinition)
    {
        if (schemaDefinition.customValidatorPath() != null)
        {
            return customExecutableValidator;
        }

        SchemaType schemaType = schemaDefinition.type();
        if (schemaType == SchemaType.CUSTOM)
        {
            return customExtensionFallbackValidator;
        }

        SchemaValidator validator = validators.get(schemaType);
        if (validator == null)
        {
            throw new IllegalArgumentException("No validator is configured for schema type: " + schemaType);
        }
        return validator;
    }
}
