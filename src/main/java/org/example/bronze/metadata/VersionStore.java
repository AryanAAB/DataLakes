package org.example.bronze.metadata;

import java.util.Optional;

public interface VersionStore
{
    Optional<FileVersion> getLatestVersion(String fileId);

    Optional<FileVersion> getLatestCheckpointVersion(String fileId);

    Optional<FileVersion> getGlobalFile(String fileId);

    void saveNewVersion(String fileId, FileVersion version);

    void saveGlobalFile(String fileId, FileVersion version);
}
