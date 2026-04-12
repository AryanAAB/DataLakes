package org.example.bronze.schemaValidation.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class FileRouter
{
    private final Path acceptedDirectory;
    private final Path rejectedDirectory;

    public FileRouter(Path acceptedDirectory, Path rejectedDirectory)
    {
        this.acceptedDirectory = acceptedDirectory;
        this.rejectedDirectory = rejectedDirectory;
    }

    public Path routeAccepted(Path file) throws IOException
    {
        return move(file, acceptedDirectory);
    }

    public Path routeRejected(Path file) throws IOException
    {
        return move(file, rejectedDirectory);
    }

    private Path move(Path file, Path targetDirectory) throws IOException
    {
        Files.createDirectories(targetDirectory);
        Path target = targetDirectory.resolve(file.getFileName());
        return Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
    }
}

