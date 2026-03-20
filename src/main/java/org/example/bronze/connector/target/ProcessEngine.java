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

public class ProcessEngine implements ProcessEngineInterface
{    
    private final DiffEngine diff;
    private Path targetRoot;
    
    public ProcessEngine(DiffEngine diff, Path targetRoot)
    {
        this->diff = diff;
        this->targetRoot= targetRoot;
    }

    public void setTargetRoot(Path targetRoot){
        this->targetRoot = targetRoot;
    }
    
    public void process(FileMetadata meta, VersionStore store, Path newFilePath) throws Exception
    {
        String fileId = meta.path();
        String fileName = meta.fileName();

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

        Path versionPath = allocate(fileId, nextVersion, snapshot);

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

        Path globalFilePath = allocateGlobal(fileId);

        Files.move(newFilePath, globalFilePath, StandardCopyOption.REPLACE_EXISTING);

        FileVersion globalFile = new FileVersion(FileType.GLOBAL, null, null,
                globalFilePath.toString(), newHash, newSize);

        store.saveGlobalFile(fileId, globalFile);
    }

    private Path allocateGlobal(String fileId) throws Exception
    {
        Path dir = targetRoot.resolve(fileId);
        Files.createDirectories(dir);

        String fileName = Path.of(fileId).getFileName().toString();
        return dir.resolve(fileName);
    }

    private Path allocate(String fileId, int version, boolean full) throws Exception
    {
        Path dir = targetRoot.resolve(fileId);
        Files.createDirectories(dir);

        return dir.resolve("v" + version + (full ? ".full" : ".delta"));
    }
}
