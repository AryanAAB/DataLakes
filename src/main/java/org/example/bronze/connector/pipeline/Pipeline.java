package org.example.bronze.connector.pipeline;

import org.example.bronze.connector.source.FileTransferResult;
import org.example.bronze.connector.source.SourceConnector;
import org.example.bronze.connector.target.TargetConnector;

public class Pipeline
{
    private final SourceConnector source;
    private final TargetConnector target;

    public Pipeline(SourceConnector source, TargetConnector target)
    {
        this.source = source;
        this.target = target;
    }

    public FileTransferResult readSource() throws Exception
    {
        return source.readAndDump();
    }

    public
}
