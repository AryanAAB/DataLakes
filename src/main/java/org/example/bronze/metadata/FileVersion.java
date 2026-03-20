package org.example.bronze.metadata;

public record FileVersion(FileType fileType, Integer version, Integer baseVersion,
                          String filePath, String hash, Long size)
{

}