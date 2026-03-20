package org.example.bronze.connector.target;

import org.example.bronze.diff.DiffEngine;
import org.example.bronze.metadata.FileMetadata;
import org.example.bronze.metadata.FileType;
import org.example.bronze.metadata.FileVersion;
import org.example.bronze.metadata.VersionStore;
import org.example.bronze.util.Constants;
import org.example.bronze.util.HashUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

public class SyncEngine implements SyncEngineInterface
{
    private static final int SNAPSHOT_INTERVAL = 2;

    private final TargetConnector source;
    private final VersionStore store;
    private final Path targetRoot;
    private final ProcessEngineInterface processEngine;

    public SyncEngine(TargetConnector source,
                      VersionStore store,
                      DiffEngine diff,
                      Path targetRoot,
                      ProcessEngineInterface processEngine)
    {
        this.source = source;
        this.store = store;
        this.diff = diff;
        this.targetRoot = targetRoot;
        this.processEngine = processEngine;
    }

    public void sync() throws Exception
    {
        try (Stream<FileMetadata> files = source.discoverFiles())
        {
            files.forEach(f ->
            {
                try
                {
                    processEngine.process(f);
                } catch (Exception e)
                {
                    Constants.logger.error("Processing failed for file " + f.fileName(), e);
                }
            });
        }
    }
}
