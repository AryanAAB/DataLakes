package org.example.bronze.diff;

import java.nio.file.Path;

public interface DiffEngine
{
    void computeDelta(Path oldFile, Path newFile, Path deltaFile) throws Exception;

    void applyDelta(Path baseFile, Path deltaFile, Path outputFile) throws Exception;
}
