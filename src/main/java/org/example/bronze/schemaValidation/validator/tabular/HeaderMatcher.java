package org.example.bronze.schemaValidation.validator.tabular;

import org.example.bronze.schemaValidation.validator.ValidationResult;

import java.util.List;

public final class HeaderMatcher
{
    public ValidationResult validateExactMatch(
            List<String> expectedHeaders,
            String expectedLocation,
            List<String> actualHeaders,
            String actualLocation
    )
    {
        if (expectedHeaders.size() != actualHeaders.size())
        {
            return ValidationResult.notMatched(
                    "Header count mismatch between %s and %s: expected %s columns but found %s"
                            .formatted(expectedLocation, actualLocation, expectedHeaders.size(), actualHeaders.size())
            );
        }

        for (int index = 0; index < expectedHeaders.size(); index++)
        {
            String expected = expectedHeaders.get(index);
            String actual = actualHeaders.get(index);
            if (!expected.equals(actual))
            {
                return ValidationResult.notMatched(
                        "Column %s mismatch between %s and %s: expected '%s' but found '%s'"
                                .formatted(index + 1, expectedLocation, actualLocation, expected, actual)
                );
            }
        }

        return ValidationResult.matched(
                "Headers matched exactly between %s and %s".formatted(expectedLocation, actualLocation)
        );
    }
}
