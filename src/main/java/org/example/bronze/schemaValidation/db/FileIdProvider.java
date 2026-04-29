package org.example.bronze.schemaValidation.db;

import org.example.bronze.schemaValidation.util.ValidationConstants;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class FileIdProvider
{
    public long resolveFileId(Connection connection, Path filePath) throws SQLException
    {
        if (filePath == null)
            throw new IllegalArgumentException("filePath cannot be null");

        PreparedStatement ps = connection.prepareStatement(ValidationConstants.GET_FILE_ID_SQL);
        ps.setString(1, filePath.toString());

        ResultSet rs = ps.executeQuery();

        if (!rs.next())
            throw new SQLException("File not found in metadata: " + filePath);

        return rs.getLong("globalFileId");
    }
}
