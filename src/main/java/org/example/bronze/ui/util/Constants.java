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

    public static final String GET_ALL_CATEGORIES= """
            SELECT category_id, category_name
            FROM pipeline_categories
            """;
}
