package org.example.bronze.schemaValidation.util;

public class ValidationConstants
{
    public static final String DELETE_EXISTING_SQL = """
            DELETE FROM mappings
            WHERE file_id = ?
            """;
    public static final String INSERT_SQL = """
            INSERT INTO mappings (file_id, schema_id)
            VALUES (?, ?)
            """;

    public static final String FIND_ALL_SQL = """
            SELECT schema_id, schema_applicable_type, schema_custom_validator_path, schema_file_path
            FROM schemas
            ORDER BY schema_id
            """;

    public static final String GET_FILE_ID_SQL = """
            SELECT global_file_id
            FROM file_meta_data
            WHERE path = ?
            """;

    public static final String GET_UNACCEPTED_SQL = """
            SELECT global_file_id, path
            FROM file_meta_data
            LEFT JOIN mappings
            ON global_file_id = file_id
            WHERE file_id is NULL
            """;
}
