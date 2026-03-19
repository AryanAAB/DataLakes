package org.example.bronze.connector.target;

import org.example.bronze.diff.DiffEngine;
import org.example.bronze.metadata.FileMetadata;
import org.example.bronze.metadata.FileVersion;
import org.example.bronze.metadata.VersionStore;
import org.example.bronze.util.Constants;
import org.example.bronze.util.HashUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SyncEngine implements SyncEngineInterface
{
    private static final int SNAPSHOT_INTERVAL = 2;

    private final TargetConnector source;
    private final VersionStore store;
    private final DiffEngine diff;
    private final Path targetRoot;

    public SyncEngine(TargetConnector source,
                      VersionStore store,
                      DiffEngine diff,
                      Path targetRoot)
    {
        this.source = source;
        this.store = store;
        this.diff = diff;
        this.targetRoot = targetRoot;
    }

    public void sync() throws Exception
    {
        try (Stream<FileMetadata> files = source.discoverFiles())
        {
            files.forEach(f ->
            {
                try
                {
                    process(f);
                } catch (Exception e)
                {
                    Constants.logger.error("Processing failed for file " + f.fileName(), e);
                }
            });
        }
    }

    private void process(FileMetadata meta) throws Exception
    {
        String fileId = meta.path();

        Path newFile = source.resolve(meta);

        String newHash = HashUtil.sha256(newFile);
        long newSize = Files.size(newFile);

        Optional<FileVersion> latestOpt = store.getLatestVersion(fileId);

        if (latestOpt.isPresent())
        {
            FileVersion latest = latestOpt.get();

            if (latest.hash().equals(newHash) && latest.size() == newSize)
                return;
        }

        int nextVersion = latestOpt.map(v -> v.version() + 1).orElse(0);
        boolean snapshot = nextVersion % SNAPSHOT_INTERVAL == 0;

        Path versionPath = allocate(fileId, nextVersion, snapshot);

        if (latestOpt.isEmpty() || snapshot)
        {
            Files.move(newFile, versionPath, StandardCopyOption.REPLACE_EXISTING);

            store.saveNewVersion(fileId,
                    new FileVersion(nextVersion, true, versionPath.toString(),
                            newHash, newSize, Instant.now()));
        }
        else
        {
            Path base = latestOpt.get().isFullSnapshot()
                    ? Path.of(latestOpt.get().filePath())
                    : reconstruct(fileId);

            diff.computeDelta(base, newFile, versionPath);

            store.saveNewVersion(fileId,
                    new FileVersion(nextVersion, false, versionPath.toString(),
                            newHash, newSize, Instant.now()));
        }
    }

    private Path allocate(String fileId, int version, boolean full) throws Exception
    {
        Path dir = targetRoot.resolve(fileId);
        Files.createDirectories(dir);

        return dir.resolve("v" + version + (full ? ".full" : ".delta"));
    }

    private Path reconstruct(String fileId) throws Exception
    {
        List<FileVersion> versions = store.getAllVersions(fileId);

        Path current = null;

        for (FileVersion v : versions)
        {
            if (v.isFullSnapshot())
            {
                current = Files.copy(Path.of(v.filePath()),
                        Files.createTempFile("base", ".tmp"),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            else
            {
                Path next = Files.createTempFile("patch", ".tmp");
                diff.applyDelta(current, Path.of(v.filePath()), next);
                current = next;
            }
        }

        return current;
    }
}
