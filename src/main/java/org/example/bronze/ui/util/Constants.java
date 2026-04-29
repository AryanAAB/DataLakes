package org.example.bronze.ui.util;

public class Constants
{
    public static final String ADD_USER = """
            INSERT INTO users (internal_id, name, isactive)
            VALUES (?, ?, ?)
            """;

    public static final String UPDATE_USER = """
            UPDATE users
            SET internal_id=?, name=?, isactive=?
            WHERE user_id=?
            """;

    public static final String GET_ALL_USERS = """
            SELECT user_id, internal_id, name, isactive
            FROM users
            """;

    public static final String ADD_CATEGORY = """
            INSERT INTO pipeline_categories (category_name)
            VALUES (?)
            """;

    public static final String UPDATE_CATEGORY = """
            UPDATE pipeline_categories
            SET category_name=?
            WHERE category_id=?
            """;

    public static final String GET_ALL_CATEGORIES = """
            SELECT category_id, category_name
            FROM pipeline_categories
            """;

    public static final String GET_ALL_TAGS = """
            SELECT tag_id, tag_name
            FROM tags
            """;

    public static final String INSERT_TAG = """
            INSERT INTO tags(tag_name)
            VALUES (?)
            """;

    public static final String ASSIGN_CATEGORY_TAG = """
            INSERT INTO category_tags(category_id, tag_id)
            VALUES (?, ?)
            """;

    public static final String DELETE_CATEGORY_TAG = """
            DELETE FROM category_tags
            WHERE category_id = ? AND tag_id = ?
            """;

    public static final String LOAD_CATEGORY_TAGS = """
            SELECT t.tag_id
            FROM tags t
            JOIN category_tags ct ON t.tag_id = ct.tag_id
            WHERE ct.category_id = ?
            """;

    public static final String ADD_PIPELINE = """
            INSERT INTO "Pipeline" ("configFilePath", "isActive", category_id, user_id)
            VALUES (?, ?, ?, ?)
            """;

    public static final String GET_ALL_PIPELINES = """
            SELECT
                p."pipelineId",
                p."configFilePath",
                c.category_name,
                u.internal_id,
                p."isActive",
                p."createdAt"
            FROM "Pipeline" p
            JOIN pipeline_categories c ON p.category_id = c.category_id
            JOIN users u ON p.user_id = u.user_id
            """;

    public static final String UPDATE_SCHEMA = """
            UPDATE schemas
            SET schema_applicable_type = CAST(? AS schema_type_enum),
                schema_custom_validator_path = ?,
                schema_file_path = ?
            WHERE schema_id = ?;
            """;

    public static final String GET_SCHEMA_TYPES = """
            SELECT unnest(enum_range(NULL::schema_type_enum));
            """;

    public static final String GET_ALL_SCHEMAS = """
            SELECT * FROM schemas;
            """;

    public static final String ADD_SCHEMA = """
            INSERT INTO schemas (
                schema_id,
                schema_applicable_type,
                schema_custom_validator_path,
                schema_file_path
            ) VALUES (?, CAST(? AS schema_type_enum), ?, ?);
            """;
}
