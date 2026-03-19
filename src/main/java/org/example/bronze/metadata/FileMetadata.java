package org.example.bronze.metadata;

import java.time.Instant;

public record FileMetadata(String path, String fileName, Instant lastModified)
{
}
