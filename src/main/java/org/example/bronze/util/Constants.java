package org.example.bronze.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants
{
    private Constants()
    {
    }

    public static final Logger logger = LoggerFactory.getLogger("Bronze");

    public static final String HASH_ALGORHTM = "SHA-256";
    public static final int HASH_BUFFER_SIZE = 8192;

    public static final String[] HDIFF_ENCODE_BASE = {
            "hdiffz",
            "-c-zstd"
    };

    public static final String HDIFF_DECODE_BASE = "hpatchz";

    public static final String GET_LATEST_VERSION = """
            SELECT *
            FROM file_versions AS f
            WHERE file_id = ?
            ORDER BY version DESC LIMIT 1
            """;

    public static final String GET_ALL_VERSIONS = """
            SELECT *
            FROM file_versions
            WHERE file_id = ?
            ORDER BY version
            """;

    public static final String SAVE_NEW_VERSION = """
            INSERT INTO file_versions VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
}
