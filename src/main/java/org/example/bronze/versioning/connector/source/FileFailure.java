package org.example.bronze.versioning.connector.source;

import java.nio.file.Path;

public record FileFailure(Path sourcePath, Path targetPath, OperationType operation, String reason, Exception exception)
{
}
