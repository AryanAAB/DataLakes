package org.example.bronze.schemaValidation.validator.tabular;

import java.util.List;

public record HeaderExtractionResult(
        String locationDescription,
        List<String> headers
)
{
}
