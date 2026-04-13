package org.example.bronze.schemaValidation.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Deprecated
public final class FileRouter
{
    private final Path acceptedDirectory;
    private final Path rejectedDirectory;

    public FileRouter(Path acceptedDirectory, Path rejectedDirectory)
    {
        this.acceptedDirectory = acceptedDirectory;
        this.rejectedDirectory = rejectedDirectory;
    }

    public Path routeAccepted(Path base, Path file) throws IOException
    {
        return move(base, file, acceptedDirectory);
    }

    public Path routeRejected(Path base, Path file) throws IOException
    {
        return move(base, file, rejectedDirectory);
    }

    private Path move(Path base, Path file, Path targetDirectory) throws IOException
    {
        Files.createDirectories(targetDirectory);
        Path relative = base.relativize(file);
        Path target = targetDirectory.resolve(relative);

        Files.createDirectories(target.getParent());
        return Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
    }
}

