package org.example.bronze.schemaValidation.validator.sql;

import java.util.Map;
import java.util.Set;

public record SqlDdlSchema(
        String tableName,
        Map<String, String> columnTypes,
        Set<String> requiredColumns
)
{
}
