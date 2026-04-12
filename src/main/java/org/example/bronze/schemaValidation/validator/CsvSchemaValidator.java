package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.validator.tabular.HeaderExtractionResult;
import org.example.bronze.schemaValidation.validator.tabular.HeaderMatcher;
import org.example.bronze.schemaValidation.validator.tabular.TabularHeaderSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvSchemaValidator implements SchemaValidator
{
    private final TabularHeaderSupport headerSupport = new TabularHeaderSupport();
    private final HeaderMatcher headerMatcher = new HeaderMatcher();

    @Override
    public ValidationResult validate(Path dataFile, SchemaDefinition schemaDefinition) throws Exception
    {
        String fileName = dataFile.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".csv"))
        {
            return ValidationResult.notMatched("CSV validation only applies to CSV files");
        }

        HeaderExtractionResult schemaHeader = findHeader(schemaDefinition.location(), "schema");
        HeaderExtractionResult dataHeader = findHeader(dataFile, "data file");

        return headerMatcher.validateExactMatch(
                schemaHeader.headers(),
                schemaHeader.locationDescription(),
                dataHeader.headers(),
                dataHeader.locationDescription()
        );
    }

    private HeaderExtractionResult findHeader(Path path, String sourceType) throws IOException
    {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8))
        {
            String line;
            int rowNumber = 0;
            while ((line = reader.readLine()) != null)
            {
                rowNumber++;
                HeaderExtractionResult header = headerSupport.extractHeader(
                        parseCsvLine(line),
                        "%s %s row %s".formatted(sourceType, path.getFileName(), rowNumber)
                );
                if (header != null)
                {
                    return header;
                }
            }
        }

        throw new IllegalArgumentException("No header row was found in %s %s".formatted(sourceType, path));
    }

    private List<String> parseCsvLine(String line)
    {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int index = 0; index < line.length(); index++)
        {
            char character = line.charAt(index);

            if (character == '"')
            {
                if (inQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"')
                {
                    current.append('"');
                    index++;
                }
                else
                {
                    inQuotes = !inQuotes;
                }
                continue;
            }

            if (character == ',' && !inQuotes)
            {
                values.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(character);
        }

        values.add(current.toString());
        return values;
    }
}
