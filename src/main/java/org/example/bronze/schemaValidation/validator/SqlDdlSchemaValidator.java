package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.validator.sql.SqlDataCompatibilityChecker;
import org.example.bronze.schemaValidation.validator.sql.SqlDdlParser;

import java.nio.file.Files;
import java.nio.file.Path;

public final class SqlDdlSchemaValidator implements SchemaValidator
{
    private final SqlDdlParser ddlParser = new SqlDdlParser();
    private final SqlDataCompatibilityChecker compatibilityChecker = new SqlDataCompatibilityChecker();

    @Override
    public ValidationResult validate(Path dataFile, SchemaDefinition schemaDefinition) throws Exception
    {
        String fileName = dataFile.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".sql"))
        {
            return ValidationResult.notMatched("SQL DDL validation only applies to SQL files");
        }

        var ddlSchema = ddlParser.parse(schemaDefinition.location());
        String sqlData = Files.readString(dataFile);
        return compatibilityChecker.validate(sqlData, ddlSchema);
    }
}
