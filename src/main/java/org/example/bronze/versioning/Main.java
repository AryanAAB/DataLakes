package org.example.bronze.versioning;

import org.example.bronze.util.Constants;
import org.example.bronze.util.DatabaseConfig;
import org.example.bronze.versioning.connector.source.LocalSourceConnector;
import org.example.bronze.versioning.connector.source.SourceConnector;
import org.example.bronze.versioning.connector.sync.SyncEngine;
import org.example.bronze.versioning.connector.target.LocalTargetConnector;
import org.example.bronze.versioning.connector.target.TargetConnector;
import org.example.bronze.versioning.diff.DiffEngine;
import org.example.bronze.versioning.diff.HDiffEngine;
import org.example.bronze.versioning.metadata.PostgresVersionStore;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Path inputPath = Path.of(Constants.PIPELINE_STAGING_DIRECTORY);

        //Path dumpPath = Path.of(Constants.VERSION_STAGING_DIRECTORY);
        //Path outputPath = Path.of("src/main/resources/bronze");

//        SourceConnector sc = new LocalSourceConnector(inputPath, dumpPath);
//        sc.readAndDump();

//        DataSource ds = DatabaseConfig.getDataSource();
//        PostgresVersionStore store = new PostgresVersionStore(ds);
//        DiffEngine diff = HDiffEngine.getInstance();
//
//        TargetConnector source = new LocalTargetConnector(dumpPath);
//
//        SyncEngine engine = new SyncEngine(source, store, diff, outputPath);
//
//        engine.sync();
    }
}