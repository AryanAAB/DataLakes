package org.example.bronze.metadata;

import org.example.bronze.util.Constants;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresVersionStore implements VersionStore
{
    private final DataSource ds;

    public PostgresVersionStore(DataSource ds)
    {
        this.ds = ds;
    }

    @Override
    public Optional<FileVersion> getLatestVersion(String fileId)
    {
        String sql = Constants.GET_LATEST_VERSION;

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql))
        {

            ps.setString(1, fileId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return Optional.of(map(rs));
            }
        } catch (Exception e)
        {
            Constants.logger.error("Could not get latest version", e);
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<FileVersion> getAllVersions(String fileId)
    {
        List<FileVersion> list = new ArrayList<>();

        String sql = Constants.GET_ALL_VERSIONS;

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, fileId);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                list.add(map(rs));
            }

        } catch (Exception e)
        {
            Constants.logger.error("Could not get all versions", e);
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public void saveNewVersion(String fileId, FileVersion v)
    {
        String sql = Constants.SAVE_NEW_VERSION;

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql))
        {

            ps.setString(1, fileId);
            ps.setInt(2, v.version());
            ps.setBoolean(3, v.isFullSnapshot());
            ps.setString(4, v.filePath());
            ps.setString(5, v.hash());
            ps.setLong(6, v.size());
            ps.setTimestamp(7, Timestamp.from(v.createdAt()));

            ps.executeUpdate();

        } catch (Exception e)
        {
            Constants.logger.error("Could not save new version", e);
            throw new RuntimeException(e);
        }
    }

    private FileVersion map(ResultSet rs) throws Exception
    {
        return new FileVersion(
                rs.getInt("version"),
                rs.getBoolean("is_full"),
                rs.getString("file_path"),
                rs.getString("hash"),
                rs.getLong("size"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
