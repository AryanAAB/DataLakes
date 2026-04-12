package org.example.bronze.schemaValidation.service;

import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.model.SchemaType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class SchemaSelectionStrategy
{
    private final DataFileTypeResolver dataFileTypeResolver = new DataFileTypeResolver();

    public List<SchemaDefinition> orderCandidates(Path dataFile, List<SchemaDefinition> schemaDefinitions)
    {
        SchemaType fileType = dataFileTypeResolver.resolve(dataFile);
        List<SchemaDefinition> ordered = new ArrayList<>(schemaDefinitions.size());

        addMatchingTypeWithCustomValidator(ordered, schemaDefinitions, fileType);
        addMatchingTypeWithDefaultValidator(ordered, schemaDefinitions, fileType);
        addCustomWithValidator(ordered, schemaDefinitions);
        addCustomWithFallback(ordered, schemaDefinitions);

        return List.copyOf(ordered);
    }

    private void addMatchingTypeWithCustomValidator(
            List<SchemaDefinition> ordered,
            List<SchemaDefinition> schemaDefinitions,
            SchemaType fileType
    )
    {
        if (fileType == null)
        {
            return;
        }

        for (SchemaDefinition schemaDefinition : schemaDefinitions)
        {
            if (schemaDefinition.type() == fileType && schemaDefinition.customValidatorPath() != null)
            {
                ordered.add(schemaDefinition);
            }
        }
    }

    private void addMatchingTypeWithDefaultValidator(
            List<SchemaDefinition> ordered,
            List<SchemaDefinition> schemaDefinitions,
            SchemaType fileType
    )
    {
        if (fileType == null)
        {
            return;
        }

        for (SchemaDefinition schemaDefinition : schemaDefinitions)
        {
            if (schemaDefinition.type() == fileType && schemaDefinition.customValidatorPath() == null)
            {
                ordered.add(schemaDefinition);
            }
        }
    }

    private void addCustomWithValidator(List<SchemaDefinition> ordered, List<SchemaDefinition> schemaDefinitions)
    {
        for (SchemaDefinition schemaDefinition : schemaDefinitions)
        {
            if (schemaDefinition.type() == SchemaType.CUSTOM && schemaDefinition.customValidatorPath() != null)
            {
                ordered.add(schemaDefinition);
            }
        }
    }

    private void addCustomWithFallback(List<SchemaDefinition> ordered, List<SchemaDefinition> schemaDefinitions)
    {
        for (SchemaDefinition schemaDefinition : schemaDefinitions)
        {
            if (schemaDefinition.type() == SchemaType.CUSTOM && schemaDefinition.customValidatorPath() == null)
            {
                ordered.add(schemaDefinition);
            }
        }
    }
}
