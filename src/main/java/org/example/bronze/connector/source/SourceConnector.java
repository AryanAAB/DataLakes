package org.example.bronze.connector.source;

import java.io.IOException;

public interface SourceConnector
{
    FileTransferResult readAndDump() throws IOException;
}
