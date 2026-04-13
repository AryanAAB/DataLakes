package org.example.bronze.versioning;

import org.example.bronze.util.Constants;

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