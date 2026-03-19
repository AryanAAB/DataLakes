package org.example.bronze.metadata;

import java.time.Instant;

public record FileVersion(long version, boolean isFullSnapshot, String filePath,
                          String hash, long size, Instant createdAt)
{
}
