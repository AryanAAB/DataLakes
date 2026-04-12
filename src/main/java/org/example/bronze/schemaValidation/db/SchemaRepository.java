package org.example.bronze.schemaValidation.db;

import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.model.SchemaType;
import org.example.bronze.schemaValidation.util.ValidationConstants;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SchemaRepository
{
    private final Connection connection;

    public SchemaRepository(Connection connection)
    {
        this.connection = connection;
    }

    public List<SchemaDefinition> findAll() throws SQLException
    {
        List<SchemaDefinition> schemaDefinitions = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(ValidationConstants.FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next())
            {
                schemaDefinitions.add(new SchemaDefinition(
                        resultSet.getString("schema_id"),
                        SchemaType.fromXmlValue(resultSet.getString("schema_applicable_type")),
                        Path.of(resultSet.getString("schema_file_path")).toAbsolutePath().normalize(),
                        nullablePath(resultSet.getString("schema_custom_validator_path"))
                ));
            }
        }

        return schemaDefinitions;
    }

    private Path nullablePath(String value)
    {
        if (value == null || value.isBlank())
        {
            return null;
        }
        return Path.of(value).toAbsolutePath().normalize();
    }
}
