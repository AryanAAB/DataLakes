package org.example.bronze.versioning.connector.target;

import org.example.bronze.versioning.metadata.FileMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface TargetConnector
{
    Stream<FileMetadata> discoverFiles() throws IOException;

    InputStream openFile(FileMetadata fileName) throws IOException;

    Path resolve(FileMetadata file);
}
