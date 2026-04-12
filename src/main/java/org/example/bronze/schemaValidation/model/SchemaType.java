package org.example.bronze.schemaValidation.model;

public enum SchemaType
{
    XSD("xsd"),
    SQL_DDL("sql_ddl"),
    CSV("csv"),
    XLSX("xlsx"),
    CUSTOM("custom");

    private final String xmlValue;

    SchemaType(String xmlValue)
    {
        this.xmlValue = xmlValue;
    }

    public static SchemaType fromXmlValue(String value)
    {
        for (SchemaType type : values())
        {
            if (type.xmlValue.equalsIgnoreCase(value))
            {
                return type;
            }
        }

        throw new IllegalArgumentException("Unsupported schema type: " + value);
    }
}
