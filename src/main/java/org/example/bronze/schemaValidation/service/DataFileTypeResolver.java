package org.example.bronze.schemaValidation.service;

import org.example.bronze.schemaValidation.model.SchemaType;

import java.nio.file.Path;

public final class DataFileTypeResolver
{
    public SchemaType resolve(Path file)
    {
        String extension = extensionOf(file);
        return switch (extension)
        {
            case "xml" -> SchemaType.XSD;
            case "sql" -> SchemaType.SQL_DDL;
            case "csv" -> SchemaType.CSV;
            case "xlsx" -> SchemaType.XLSX;
            default -> null;
        };
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
