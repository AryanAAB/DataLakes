package org.example.bronze.versioning.connector.target;

import org.example.bronze.util.Constants;
import org.example.bronze.util.DatabaseConfig;
import org.example.bronze.versioning.metadata.FileMetadata;
import org.example.bronze.versioning.util.VersioningConstants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class LocalTargetConnector implements TargetConnector
{
    private final Path root;

    public LocalTargetConnector(final Path root)
    {
        this.root = root;
    }

    @Override
    public Stream<FileMetadata> discoverFiles() throws IOException
    {
        List<FileMetadata> result = new ArrayList<>();

        Constants.logger.info("Fetching all files for versioning");

        try (Connection connection = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = connection.prepareStatement(VersioningConstants.GET_ALL_FILES);
             ResultSet rs = stmt.executeQuery())
        {

            while (rs.next())
            {
                result.add(new FileMetadata(
                        rs.getLong("global_file_id"),
                        rs.getString("path"),
                        rs.getTimestamp("modified_time").toInstant()
                ));
            }

        } catch (SQLException e)
        {
            throw new IOException(e);
        }

        return result.stream();
    }

    @Override
    public InputStream openFile(FileMetadata file) throws IOException
    {
        return Files.newInputStream(resolve(file));
    }

    @Override
    public Path resolve(FileMetadata file)
    {
        return root.resolve(file.filePath());
    }
}
