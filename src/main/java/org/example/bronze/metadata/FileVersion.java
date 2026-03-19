package org.example.bronze.metadata;

import java.time.Instant;

public record FileVersion(int version, boolean isFullSnapshot, String filePath,
                          String hash, long size, Instant createdAt)
{
}
