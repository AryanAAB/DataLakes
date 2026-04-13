package org.example.bronze.versioning.metadata;

import org.example.bronze.util.Constants;

import java.nio.file.Path;
import java.time.Instant;

public record FileMetadata(Long fileId, String filePath, Instant lastModifiedTime)
{
    public FileMetadata(Long fileId, String filePath, Instant lastModifiedTime)
    {
        Path relativeFilePath = Path.of(Constants.PIPELINE_STAGING_DIRECTORY).toAbsolutePath().relativize(Path.of(filePath));
        this.fileId = fileId;
        this.filePath = relativeFilePath.toString();
        this.lastModifiedTime = lastModifiedTime;
    }
}
