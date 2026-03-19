package org.example.bronze.connector.source;

import java.util.List;

public record FileTransferResult(long totalFiles, long successfulFiles, List<FileFailure> failures)
{
    public boolean isSuccess()
    {
        return failures.isEmpty();
    }
}
