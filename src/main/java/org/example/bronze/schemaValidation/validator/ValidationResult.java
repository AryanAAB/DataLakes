package org.example.bronze.schemaValidation.validator;

public record ValidationResult(
        boolean matched,
        String message
)
{
    public static ValidationResult matched(String message)
    {
        return new ValidationResult(true, message);
    }

    public static ValidationResult notMatched(String message)
    {
        return new ValidationResult(false, message);
    }
}
