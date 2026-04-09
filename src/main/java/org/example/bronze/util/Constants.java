package org.example.bronze.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants
{
    public static final String GET_PIPELINES = """
            SELECT "pipelineId", "configFilePath" FROM "Pipeline" WHERE "isActive" = true
            """;

    public static final String ADD_METADATA = """
            INSERT INTO "FileMetaData"
            ("pipelineId", "id", "parentId", "name", "mimeType", "exportMimeType",
             "size", "createdTime", "modifiedTime")
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT ("pipelineId", "id")
            DO UPDATE SET
                "parentId" = EXCLUDED."parentId",
                "name" = EXCLUDED."name",
                "mimeType" = EXCLUDED."mimeType",
                "exportMimeType" = EXCLUDED."exportMimeType",
                "size" = EXCLUDED."size",
                "createdTime" = EXCLUDED."createdTime",
                "modifiedTime" = EXCLUDED."modifiedTime";
            """;

    public static final Logger logger = LoggerFactory.getLogger("BronzeIngestion");
}