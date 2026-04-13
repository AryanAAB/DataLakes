package org.example.bronze.versioning.diff;

import org.example.bronze.util.Constants;
import org.example.bronze.versioning.util.VersioningConstants;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HDiffEngine implements DiffEngine
{
    private HDiffEngine()
    {
    }

    private static class Holder
    {
        private static final HDiffEngine INSTANCE = new HDiffEngine();
    }

    public static HDiffEngine getInstance()
    {
        return Holder.INSTANCE;
    }

    @Override
    public void computeDelta(Path oldFile, Path newFile, Path deltaFile) throws Exception
    {
        List<String> cmd = new ArrayList<>(Arrays.asList(VersioningConstants.HDIFF_ENCODE_BASE));
        cmd.add(oldFile.toString());
        cmd.add(newFile.toString());
        cmd.add(deltaFile.toString());

        Process p = new ProcessBuilder(cmd).start();

        if (p.waitFor() != 0)
        {
            Constants.logger.error("xdelta encode failed for file {} to {} into {}.", oldFile, newFile, deltaFile);
            throw new RuntimeException("xdelta encode failed.");
        }
    }

    @Override
    public void applyDelta(Path baseFile, Path deltaFile, Path outputFile) throws Exception
    {
        Process p = new ProcessBuilder(
                VersioningConstants.HDIFF_DECODE_BASE,
                baseFile.toString(),
                deltaFile.toString(),
                outputFile.toString()
        ).start();

        if (p.waitFor() != 0)
        {
            Constants.logger.error("xdelta decode failed for file {} to {} into {}.", baseFile, deltaFile, outputFile);
            throw new RuntimeException("xdelta decode failed.");
        }
    }
}
