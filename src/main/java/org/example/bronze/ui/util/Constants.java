package org.example.bronze.ui.util;

public class Constants
{
    public static final String ADD_USER = """
            INSERT INTO users (internal_id, name, is_active)
            VALUES (?, ?, ?)
            """;

    public static final String UPDATE_USER = """
            UPDATE users
            SET internal_id=?, name=?, is_active=?
            WHERE user_id=?
            """;

    public static final String GET_ALL_USERS = """
            SELECT user_id, internal_id, name, is_active
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
            INSERT INTO pipeline (config_file_path, is_active, category_id, user_id)
            VALUES (?, ?, ?, ?)
            """;

    public static final String GET_ALL_PIPELINES = """
            SELECT
                p.pipeline_id,
                p.config_file_path,
                c.category_name,
                u.internal_id,
                p.is_active,
                p.created_at
            FROM pipeline p
            JOIN pipeline_categories c ON p.category_id = c.category_id
            JOIN users u ON p.user_id = u.user_id
            """;
}
