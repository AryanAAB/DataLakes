package org.example.bronze.ingestion.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.drive.Drive;
import org.example.bronze.ingestion.connector.GoogleDriveSourceConnector;
import org.example.bronze.ingestion.connector.SourceConnector;
import org.example.bronze.ingestion.util.DriveFactory;

import java.nio.file.Path;

public class PipelineFactory
{
    public static IngestionPipeline createPipelineFromJson(JsonNode root, String connector, long pipelineId) throws Exception
    {
        switch (connector)
        {
            case "GoogleDriveSourceConnector":
                return createDrivePipeline(root, pipelineId);
            default:
                return null;
        }
    }

    private static IngestionPipeline createDrivePipeline(JsonNode root, long pipelineId) throws Exception
    {
        String pipelineName = root.get("pipelineName").asText();
        String credentialPath = root.get("credentialPath").asText();
        String stagePath = root.get("stageDirectoryPath").asText();

        Drive drive = DriveFactory.createDriveService(credentialPath);
        SourceConnector driveConnector = new GoogleDriveSourceConnector(drive);
        Path outputDirectory = Path.of(stagePath);

        return new LocalIngestionPipeline(driveConnector, outputDirectory, pipelineName, pipelineId);
    }
}