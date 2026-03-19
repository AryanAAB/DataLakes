package org.example.bronze.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig
{
    public static DataSource createDataSource()
    {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(AppConfig.get("db.url"));
        config.setUsername(AppConfig.get("db.user"));
        config.setPassword(AppConfig.get("db.password"));

        config.setMaximumPoolSize(
                Integer.parseInt(AppConfig.get("db.pool.size"))
        );

        return new HikariDataSource(config);
    }

    private DatabaseConfig() {}
}