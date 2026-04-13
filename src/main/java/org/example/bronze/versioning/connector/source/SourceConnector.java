package org.example.bronze.versioning.connector.source;

import java.io.IOException;

@Deprecated
public interface SourceConnector
{
    FileTransferResult readAndDump() throws IOException;
}
