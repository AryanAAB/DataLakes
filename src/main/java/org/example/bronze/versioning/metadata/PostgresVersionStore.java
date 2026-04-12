package org.example.bronze.versioning.metadata;

import org.example.bronze.util.Constants;
import org.example.bronze.versioning.util.VersioningConstants;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        String sql = VersioningConstants.GET_LATEST_VERSION;

        return get(sql, fileId);
    }

    @Override
    public Optional<FileVersion> getLatestCheckpointVersion(String fileId)
    {
        String sql = VersioningConstants.GET_LATEST_CHECKPOINT_VERSION;

        return get(sql, fileId);
    }

    @Override
    public Optional<FileVersion> getGlobalFile(String fileId)
    {
        String sql = VersioningConstants.GET_GLOBAL_FILE;

        return get(sql, fileId);
    }

    private Optional<FileVersion> get(String sql, String fileId)
    {
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
            Constants.logger.error("Could not get file", e);
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void saveNewVersion(String fileId, FileVersion v)
    {
        String sql = VersioningConstants.SAVE_VERSION_FILE;
        save(sql, fileId, v);
    }

    @Override
    public void saveGlobalFile(String fileId, FileVersion v)
    {
        String sql = VersioningConstants.SAVE_GLOBAL_FILE;
        save(sql, fileId, v);
    }

    private void save(String sql, String fileId, FileVersion v)
    {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, fileId);
            ps.setString(2, v.fileType().toString());
            ps.setObject(3, v.version());
            ps.setObject(4, v.baseVersion());
            ps.setString(5, v.filePath());
            ps.setString(6, v.hash());
            ps.setLong(7, v.size());

            ps.executeUpdate();

        } catch (Exception e)
        {
            Constants.logger.error("Could not save new version", e);
            throw new RuntimeException(e);
        }
    }

    private FileVersion map(ResultSet rs) throws Exception
    {
        Integer version = rs.getObject("version", Integer.class);
        Integer baseVersion = rs.getObject("base_version", Integer.class);

        return new FileVersion(
                FileType.valueOf(rs.getString("file_type")),
                version,
                baseVersion,
                rs.getString("file_path"),
                rs.getString("hash"),
                rs.getLong("size")
        );
    }
}
