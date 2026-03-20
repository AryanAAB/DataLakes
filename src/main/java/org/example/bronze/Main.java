package org.example.bronze;

import org.example.bronze.config.DatabaseConfig;
import org.example.bronze.connector.source.LocalSourceConnector;
import org.example.bronze.connector.source.SourceConnector;
import org.example.bronze.connector.target.LocalTargetConnector;
import org.example.bronze.connector.target.SyncEngine;
import org.example.bronze.connector.target.TargetConnector;
import org.example.bronze.diff.DiffEngine;
import org.example.bronze.diff.HDiffEngine;
import org.example.bronze.metadata.PostgresVersionStore;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Path inputPath = Path.of("src/main/resources/input");

        Path dumpPath = Path.of("src/main/resources/dump");
        Path outputPath = Path.of("src/main/resources/bronze");

        Files.createDirectories(dumpPath);
        Files.createDirectories(outputPath);

        SourceConnector sc = new LocalSourceConnector(inputPath, dumpPath);
        sc.readAndDump();

        DataSource ds = DatabaseConfig.createDataSource();
        PostgresVersionStore store = new PostgresVersionStore(ds);
        DiffEngine diff = HDiffEngine.getInstance();

        TargetConnector source = new LocalTargetConnector(dumpPath);

        ProcessEngine processEngine = new ProcessEngine();

        SyncEngine engine = new SyncEngine(source, store, diff, outputPath, processEngine);

        engine.sync();
    }
}
