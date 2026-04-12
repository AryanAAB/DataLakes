package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CustomExecutableSchemaValidator implements SchemaValidator
{
    @Override
    public ValidationResult validate(Path dataFile, SchemaDefinition schemaDefinition) throws Exception
    {
        Path customValidatorPath = schemaDefinition.customValidatorPath();
        if (customValidatorPath == null)
        {
            return ValidationResult.notMatched("No custom validator path is configured for this schema");
        }
        if (Files.notExists(customValidatorPath))
        {
            return ValidationResult.notMatched("Custom validator does not exist: " + customValidatorPath);
        }

        List<String> command = new ArrayList<>();
        command.add(customValidatorPath.toString());
        command.add(dataFile.toString());
        command.add(schemaDefinition.location().toString());

        Process process = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();

        String output;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)))
        {
            output = reader.lines().reduce("", (left, right) -> left.isEmpty() ? right : left + System.lineSeparator() + right);
        }

        int exitCode = process.waitFor();
        String normalizedOutput = output.trim().toLowerCase();
        if (exitCode == 0 && "true".equals(normalizedOutput))
        {
            return ValidationResult.matched("Custom validator accepted the file");
        }

        if ("false".equals(normalizedOutput))
        {
            return ValidationResult.notMatched("Custom validator rejected the file");
        }

        return ValidationResult.notMatched(
                "Custom validator returned exit code %s with output: %s".formatted(exitCode, output.trim())
        );
    }
}
