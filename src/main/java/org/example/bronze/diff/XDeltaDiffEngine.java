package org.example.bronze.diff;

import org.example.bronze.util.Constants;

import java.io.IOException;
import java.nio.file.Path;

public class XDeltaDiffEngine implements DiffEngine
{
    @Override
    public void computeDelta(Path oldFile, Path newFile, Path deltaFile) throws Exception
    {
        Process p = new ProcessBuilder(
                Constants.xDeltaEncodeCommand,
                oldFile.toString(),
                newFile.toString(),
                deltaFile.toString()
        ).start();

        if(p.waitFor() != 0)
        {
            Constants.logger.error("xdelta encode failed for file " + oldFile + " to " + newFile + " into " + deltaFile + ".");
            throw new RuntimeException("xdelta encode failed.");
        }
    }

    @Override
    public void applyDelta(Path baseFile, Path deltaFile, Path outputFile) throws Exception
    {
        Process p = new ProcessBuilder(
            Constants.xDeltaDecodeCommand,
            baseFile.toString(),
            deltaFile.toString(),
            outputFile.toString()
        ).start();

        if(p.waitFor() != 0)
        {
            Constants.logger.error("xdelta decode failed for file " + baseFile + " to " + deltaFile + " into " + outputFile + ".");
            throw new RuntimeException("xdelta decode failed.");
        }
    }
}
