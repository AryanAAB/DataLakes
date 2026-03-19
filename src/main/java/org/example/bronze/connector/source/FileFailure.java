package org.example.bronze.connector.source;

import java.nio.file.Path;

public record FileFailure(Path sourcePath, Path targetPath, OperationType operation, String reason, Exception exception)
{
}
