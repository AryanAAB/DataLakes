package org.example.bronze.versioning.metadata;

import java.util.Optional;

public interface VersionStore
{
    Optional<FileVersion> getLatestVersion(long fileId);

    Optional<FileVersion> getLatestCheckpointVersion(long fileId);

    Optional<FileVersion> getGlobalFile(long fileId);

    void saveNewVersion(long fileId, FileVersion version);

    void saveGlobalFile(long fileId, FileVersion version);
}
