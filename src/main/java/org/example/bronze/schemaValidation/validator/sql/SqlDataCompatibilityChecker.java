package org.example.bronze.schemaValidation.validator.sql;

import org.example.bronze.schemaValidation.validator.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqlDataCompatibilityChecker
{
    private static final Pattern INSERT_PATTERN = Pattern.compile(
            "insert\\s+into\\s+([a-zA-Z_][\\w]*)\\s*\\(([^)]*)\\)\\s*values\\s*\\(([^;]*)\\)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    public ValidationResult validate(String sqlData, SqlDdlSchema ddlSchema)
    {
        String normalizedSql = sqlData.replaceAll("(?m)--.*$", "");
        Matcher matcher = INSERT_PATTERN.matcher(normalizedSql);
        List<String> insertStatements = new ArrayList<>();

        while (matcher.find())
        {
            insertStatements.add(matcher.group());
            String tableName = matcher.group(1).toLowerCase(Locale.ROOT);
            if (!tableName.equals(ddlSchema.tableName()))
            {
                return ValidationResult.notMatched(
                        "INSERT targets table %s but DDL expects %s".formatted(tableName, ddlSchema.tableName())
                );
            }

            String[] columns = matcher.group(2).split(",");
            Set<String> requiredColumns = ddlSchema.requiredColumns();
            List<String> presentColumns = new ArrayList<>();

            for (String rawColumn : columns)
            {
                String columnName = rawColumn.trim().toLowerCase(Locale.ROOT);
                presentColumns.add(columnName);
                if (!ddlSchema.columnTypes().containsKey(columnName))
                {
                    return ValidationResult.notMatched("Column %s is not defined in DDL".formatted(columnName));
                }
            }

            if (!presentColumns.containsAll(requiredColumns))
            {
                return ValidationResult.notMatched(
                        "INSERT statement does not include all NOT NULL columns required by the DDL"
                );
            }
        }

        if (insertStatements.isEmpty())
        {
            return ValidationResult.notMatched("No INSERT statements compatible with DDL checking were found");
        }

        return ValidationResult.matched("SQL file is compatible with DDL for table %s".formatted(ddlSchema.tableName()));
    }
}
