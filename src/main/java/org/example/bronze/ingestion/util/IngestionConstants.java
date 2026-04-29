package org.example.bronze.ingestion.util;

public class IngestionConstants
{
    public static final String PIPELINE_NAME = "pipelineName";
    public static final String STAGE_DIRECTORY_PATH = "stageDirectoryPath";
    public static final String GDRIVE_CREDENTIALS = "credentialPath";

    public static final String GET_PIPELINES = """
            SELECT pipeline_id, config_file_path FROM pipeline WHERE is_active = true
            """;

    public static final String ADD_METADATA = """
            INSERT INTO file_meta_data
            (pipeline_id, id, parent_id, name, path, mime_type, export_mime_type,
             size, created_time, modified_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (pipeline_id, id)
            DO UPDATE SET
                parent_id = EXCLUDED.parent_id,
                name = EXCLUDED.name,
                path = EXCLUDED.path,
                mime_type = EXCLUDED.mime_type,
                export_mime_type = EXCLUDED.export_mime_type,
                size = EXCLUDED.size,
                created_time = EXCLUDED.created_time,
                modified_time = EXCLUDED.modified_time;
            """;

    public static final String CHECK_MODIFIED = """
            SELECT 1
            FROM file_meta_data
            WHERE pipeline_id = ?
            AND id = ?
            AND modified_time = ?
            """;
}
