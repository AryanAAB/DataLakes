package org.example.bronze.versioning.connector.sync;

import org.example.bronze.util.Constants;
import org.example.bronze.versioning.connector.target.TargetConnector;
import org.example.bronze.versioning.diff.DiffEngine;
import org.example.bronze.versioning.metadata.FileMetadata;
import org.example.bronze.versioning.metadata.FileType;
import org.example.bronze.versioning.metadata.FileVersion;
import org.example.bronze.versioning.metadata.VersionStore;
import org.example.bronze.versioning.util.HashUtil;
import org.example.bronze.versioning.util.VersioningConstants;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

public class SyncEngine implements Syncable
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

    @Override
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
                    Constants.logger.error("Processing failed for file {}", f.fileId(), e);
                }
            });
        }
        catch(Exception e)
        {
            Constants.logger.error("No files found", e);
        }
    }

    private void process(FileMetadata meta) throws Exception
    {
        long fileId = meta.fileId();

        Path newFilePath = source.resolve(meta);

        String newHash = HashUtil.sha256(newFilePath);
        long newSize = Files.size(newFilePath);

        Optional<FileVersion> latestOpt = store.getLatestVersion(fileId);

        if (latestOpt.isPresent())
        {
            FileVersion latest = latestOpt.get();

            if (latest.hash().equals(newHash) && latest.size() == newSize)
                return;
        }

        int nextVersion = latestOpt.map(v -> v.version() + 1).orElse(0);
        boolean snapshot = nextVersion % SNAPSHOT_INTERVAL == 0;

        Path versionPath = allocate(meta.filePath(), nextVersion, snapshot);

        if (latestOpt.isEmpty() || snapshot)
        {
            Files.copy(newFilePath, versionPath, StandardCopyOption.REPLACE_EXISTING);

            store.saveNewVersion(fileId,
                    new FileVersion(FileType.CHECKPOINT, nextVersion, null,
                            versionPath.toString(), newHash, newSize));
        }
        else
        {
            Optional<FileVersion> globalFile = store.getGlobalFile(fileId);
            Optional<FileVersion> lastCheckpointFile = store.getLatestCheckpointVersion(fileId);

            if (globalFile.isEmpty()) throw new RuntimeException("Global file does not exist");
            else if (lastCheckpointFile.isEmpty()) throw new RuntimeException("Last checkpoint file does not exist");

            diff.computeDelta(Path.of(globalFile.get().filePath()), newFilePath, versionPath);

            store.saveNewVersion(fileId,
                    new FileVersion(FileType.DIFF, nextVersion, lastCheckpointFile.get().version(),
                            versionPath.toString(), newHash, newSize));
        }

        Path globalFilePath = allocateGlobal(meta.filePath());

        Files.copy(newFilePath, globalFilePath, StandardCopyOption.REPLACE_EXISTING);

        FileVersion globalFile = new FileVersion(FileType.GLOBAL, null, null,
                globalFilePath.toString(), newHash, newSize);

        store.saveGlobalFile(fileId, globalFile);
    }

    private Path allocateGlobal(String filePath) throws Exception
    {
        Path dir = targetRoot.resolve(filePath);
        Files.createDirectories(dir);

        String fileName = Path.of(filePath).getFileName().toString();
        return dir.resolve(fileName);
    }

    private Path allocate(String filePath, int version, boolean full) throws Exception
    {
        Path dir = targetRoot.resolve(filePath);
        Files.createDirectories(dir);

        return dir.resolve("v" + version + (full ? ".full" : ".delta"));
    }
}