package org.example.bronze.versioning.metadata;

import java.time.Instant;

public record FileMetadata(String path, String fileName, Instant lastModified)
{
}
