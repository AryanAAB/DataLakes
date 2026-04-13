package org.example.bronze.versioning.util;

public class VersioningConstants
{
    public static final String GET_ALL_FILES = """
            SELECT "globalFileId", "path", "modifiedTime"
            FROM "FileMetaData"
            WHERE path IS NOT NULL
            """;

    public static final String GET_GLOBAL_FILE = """
            SELECT *
            FROM file_objects
            WHERE file_id = ? AND file_type = 'GLOBAL'
            """;

    public static final String GET_LATEST_VERSION = """
            SELECT *
            FROM file_objects
            WHERE file_id = ?
                AND version IS NOT NULL
            ORDER BY version DESC
            LIMIT 1
            """;

    public static final String GET_LATEST_CHECKPOINT_VERSION = """
            SELECT *
            FROM file_objects
            WHERE file_id = ?
              AND file_type = 'CHECKPOINT'
            ORDER BY version DESC
            LIMIT 1
            """;

    public static final String SAVE_VERSION_FILE = """
            INSERT INTO file_objects (
            file_id,
            file_type,
            version,
            base_version,
            file_path,
            hash,
            size
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String SAVE_GLOBAL_FILE = """
            INSERT INTO file_objects (
                file_id,
                file_type,
                version,
                base_version,
                file_path,
                hash,
                size
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (file_id)
            WHERE file_type = 'GLOBAL'
            DO UPDATE SET
                hash = EXCLUDED.hash,
                size = EXCLUDED.size;
            """;

    public static final String HASH_ALGORHTM = "SHA-256";
    public static final int HASH_BUFFER_SIZE = 8192;
    public static final String[] HDIFF_ENCODE_BASE = {
            "hdiffz",
            "-c-zstd"
    };

    public static final String HDIFF_DECODE_BASE = "hpatchz";

    public static final int SNAPSHOT_INTERVAL = 5;
}
