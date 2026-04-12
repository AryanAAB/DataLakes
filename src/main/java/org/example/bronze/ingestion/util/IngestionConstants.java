package org.example.bronze.ingestion.util;

public class IngestionConstants
{
    public static final String CONNECTOR_NAME = "connector";
    public static final String PIPELINE_NAME = "pipelineName";
    public static final String STAGE_DIRECTORY_PATH = "stageDirectoryPath";
    public static final String GDRIVE_CREDENTIALS = "credentialPath";

    public static final String GET_PIPELINES = """
            SELECT "pipelineId", "configFilePath" FROM "Pipeline" WHERE "isActive" = true
            """;

    public static final String ADD_METADATA = """
            INSERT INTO "FileMetaData"
            ("pipelineId", "id", "parentId", "name", "path", "mimeType", "exportMimeType",
             "size", "createdTime", "modifiedTime")
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT ("pipelineId", "id")
            DO UPDATE SET
                "parentId" = EXCLUDED."parentId",
                "name" = EXCLUDED."name",
                "path" = EXCLUDED."path",
                "mimeType" = EXCLUDED."mimeType",
                "exportMimeType" = EXCLUDED."exportMimeType",
                "size" = EXCLUDED."size",
                "createdTime" = EXCLUDED."createdTime",
                "modifiedTime" = EXCLUDED."modifiedTime";
            """;

    public static final String CHECK_MODIFIED = """
            SELECT 1
            FROM "FileMetaData"
            WHERE "pipelineId" = ?
            AND id = ?
            AND "modifiedTime" = ?
            """;
}
