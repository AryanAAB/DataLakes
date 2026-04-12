package org.example.bronze.schemaValidation.validator.sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqlDdlParser
{
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(
            "create\\s+table\\s+([a-zA-Z_][\\w]*)\\s*\\((.*)\\)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    private static final Pattern COLUMN_PATTERN = Pattern.compile(
            "^([a-zA-Z_][\\w]*)\\s+([a-zA-Z]+(?:\\s*\\([^)]*\\))?).*$",
            Pattern.CASE_INSENSITIVE
    );

    public SqlDdlSchema parse(Path ddlFile) throws IOException
    {
        String ddl = Files.readString(ddlFile)
                .replaceAll("(?m)--.*$", "")
                .trim();

        Matcher matcher = CREATE_TABLE_PATTERN.matcher(ddl);
        if (!matcher.find())
        {
            throw new IllegalArgumentException("Only CREATE TABLE DDL files are supported: " + ddlFile);
        }

        String tableName = matcher.group(1).toLowerCase();
        String columnsSection = matcher.group(2);

        Map<String, String> columnTypes = new LinkedHashMap<>();
        Set<String> requiredColumns = new LinkedHashSet<>();

        for (String rawColumn : splitColumns(columnsSection))
        {
            String columnDefinition = rawColumn.trim();
            if (columnDefinition.isBlank())
            {
                continue;
            }

            String normalized = columnDefinition.toLowerCase();
            if (normalized.startsWith("primary key")
                    || normalized.startsWith("foreign key")
                    || normalized.startsWith("constraint")
                    || normalized.startsWith("unique")
                    || normalized.startsWith("check"))
            {
                continue;
            }

            Matcher columnMatcher = COLUMN_PATTERN.matcher(columnDefinition);
            if (!columnMatcher.matches())
            {
                continue;
            }

            String columnName = columnMatcher.group(1).toLowerCase();
            String columnType = normalizeType(columnMatcher.group(2));
            columnTypes.put(columnName, columnType);

            if (normalized.contains("not null"))
            {
                requiredColumns.add(columnName);
            }
        }

        if (columnTypes.isEmpty())
        {
            throw new IllegalArgumentException("No columns were parsed from DDL file: " + ddlFile);
        }

        return new SqlDdlSchema(tableName, Map.copyOf(columnTypes), Set.copyOf(requiredColumns));
    }

    private String[] splitColumns(String columnsSection)
    {
        return columnsSection.split(",(?![^()]*\\))");
    }

    private String normalizeType(String rawType)
    {
        return rawType.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
