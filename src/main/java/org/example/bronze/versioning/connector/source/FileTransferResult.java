package org.example.bronze.versioning.connector.source;

import java.util.List;

@Deprecated
public record FileTransferResult(long totalFiles, long successfulFiles, List<FileFailure> failures)
{
    public boolean isSuccess()
    {
        return failures.isEmpty();
    }
}
