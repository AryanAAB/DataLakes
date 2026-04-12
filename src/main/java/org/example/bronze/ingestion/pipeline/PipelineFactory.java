package org.example.bronze.ingestion.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.drive.Drive;
import org.example.bronze.ingestion.connector.GoogleDriveSourceConnector;
import org.example.bronze.ingestion.connector.SourceConnector;
import org.example.bronze.ingestion.util.DriveFactory;
import org.example.bronze.ingestion.util.IngestionConstants;
import org.example.bronze.util.Constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PipelineFactory
{
    public static IngestionPipeline createPipelineFromJson(JsonNode root, String connector, long pipelineId) throws Exception
    {
        String stagePath = root.get(IngestionConstants.STAGE_DIRECTORY_PATH).asText();

        Path stagePathObj = Paths.get(
                Constants.PIPELINE_STAGING_DIRECTORY,
                String.valueOf(pipelineId),
                stagePath
        ).normalize();

        stagePath = stagePathObj.toString();

        switch (connector)
        {
            case "GoogleDriveSourceConnector":
                return createDrivePipeline(root, pipelineId, stagePath);
            default:
                return null;
        }
    }

    private static IngestionPipeline createDrivePipeline(JsonNode root, long pipelineId, String stagePath) throws Exception
    {
        String pipelineName = root.get(IngestionConstants.PIPELINE_NAME).asText();
        String credentialPath = root.get(IngestionConstants.GDRIVE_CREDENTIALS).asText();

        Drive drive = DriveFactory.createDriveService(credentialPath);
        SourceConnector driveConnector = new GoogleDriveSourceConnector(drive);
        Path outputDirectory = Path.of(stagePath);
        return new LocalIngestionPipeline(driveConnector, outputDirectory, pipelineName, pipelineId);
    }
}