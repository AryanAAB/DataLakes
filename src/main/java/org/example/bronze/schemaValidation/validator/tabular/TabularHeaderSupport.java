package org.example.bronze.schemaValidation.validator.tabular;

import java.util.ArrayList;
import java.util.List;

public final class TabularHeaderSupport
{
    public HeaderExtractionResult extractHeader(List<String> cells, String locationDescription)
    {
        List<String> normalizedCells = new ArrayList<>(cells.size());
        for (String cell : cells)
        {
            normalizedCells.add(normalize(cell));
        }

        int firstNonBlank = -1;
        for (int index = 0; index < normalizedCells.size(); index++)
        {
            if (!normalizedCells.get(index).isEmpty())
            {
                firstNonBlank = index;
                break;
            }
        }

        if (firstNonBlank < 0)
        {
            return null;
        }

        List<String> headers = new ArrayList<>();
        boolean encounteredBlankAfterHeader = false;

        for (int index = firstNonBlank; index < normalizedCells.size(); index++)
        {
            String value = normalizedCells.get(index);
            if (value.isEmpty())
            {
                encounteredBlankAfterHeader = true;
                continue;
            }

            if (encounteredBlankAfterHeader)
            {
                throw new IllegalArgumentException(
                        "Non-contiguous header cells found at %s; header cells must appear in one block"
                                .formatted(locationDescription)
                );
            }

            headers.add(value);
        }

        if (headers.isEmpty())
        {
            return null;
        }

        return new HeaderExtractionResult(locationDescription, List.copyOf(headers));
    }

    private String normalize(String value)
    {
        return value == null ? "" : value.trim();
    }
}
