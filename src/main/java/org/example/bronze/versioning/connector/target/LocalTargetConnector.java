package org.example.bronze.versioning.connector.target;

import org.example.bronze.versioning.metadata.FileMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LocalTargetConnector implements TargetConnector
{
    private final Path root;

    public LocalTargetConnector(final Path root)
    {
        this.root = root;
    }

    @Override
    public Stream<FileMetadata> discoverFiles() throws IOException
    {
        return Files.walk(root)
                .filter(Files::isRegularFile)
                .map(path ->
                {
                    try
                    {
                        return new FileMetadata(
                                root.relativize(path).toString(),
                                path.getFileName().toString(),
                                Files.getLastModifiedTime(path).toInstant()
                        );
                    } catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public InputStream openFile(FileMetadata file) throws IOException
    {
        return Files.newInputStream(resolve(file));
    }

    @Override
    public Path resolve(FileMetadata file)
    {
        return root.resolve(file.path());
    }
}
