package org.example.bronze.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig
{
    private static final HikariDataSource dataSource;

    static
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConfig.get("db.url"));
        config.setUsername(AppConfig.get("db.user"));
        config.setPassword(AppConfig.get("db.password"));

        config.setMaximumPoolSize(
                Integer.parseInt(AppConfig.get("db.pool.size"))
        );

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource()
    {
        return dataSource;
    }

    private DatabaseConfig()
    {
    }
}