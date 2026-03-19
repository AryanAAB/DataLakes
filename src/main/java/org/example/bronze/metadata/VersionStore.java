package org.example.bronze.metadata;

import java.util.List;
import java.util.Optional;

public interface VersionStore
{
    Optional<FileVersion> getLatestVersion(String fileId);

    List<FileVersion> getAllVersions(String fileId);

    void saveNewVersion(String fileId, FileVersion version);
}
