package org.example.bronze.schemaValidation.db;

import org.example.bronze.schemaValidation.model.SchemaDefinition;
import org.example.bronze.schemaValidation.util.ValidationConstants;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class MappingRepository
{
    private final Connection connection;
    private final FileIdProvider fileIdProvider;

    public MappingRepository(Connection connection, FileIdProvider fileIdProvider)
    {
        this.connection = connection;
        this.fileIdProvider = fileIdProvider;
    }

    public void insertMapping(Path filePath, SchemaDefinition schemaDefinition) throws SQLException
    {
        long fileId = fileIdProvider.resolveFileId(connection, filePath);

        try (PreparedStatement statement = connection.prepareStatement(ValidationConstants.DELETE_EXISTING_SQL))
        {
            statement.setLong(1, fileId);
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(ValidationConstants.INSERT_SQL))
        {
            statement.setLong(1, fileId);
            statement.setString(2, schemaDefinition.id());
            statement.executeUpdate();
        }
    }
}
