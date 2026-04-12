package org.example.bronze.schemaValidation.validator;

import org.example.bronze.schemaValidation.model.SchemaDefinition;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.nio.file.Path;

public final class XsdSchemaValidator implements SchemaValidator
{
    @Override
    public ValidationResult validate(Path dataFile, SchemaDefinition schemaDefinition)
    {
        String fileName = dataFile.getFileName().toString().toLowerCase();
        if (!fileName.endsWith(".xml"))
        {
            return ValidationResult.notMatched("XSD validation only applies to XML files");
        }

        try
        {
            var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            var schema = schemaFactory.newSchema(schemaDefinition.location().toFile());
            var validator = schema.newValidator();
            validator.validate(new StreamSource(dataFile.toFile()));
            return ValidationResult.matched("XML is valid for schema %s".formatted(schemaDefinition.id()));
        } catch (Exception exception)
        {
            return ValidationResult.notMatched(exception.getMessage());
        }
    }
}
