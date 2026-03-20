/**
 *  Utility class for loading and accessing database configuration properties.
 *  This class connects to the database as provided in the environment file.
 *
 * @author Aryan
 * @author Abhirath
 * @author Kavya
 * @author Nainika
 * @version 1.0
 * @since 1.0
 */

package org.example.bronze.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig
{
    /**
     * Creates a datasource based on the configurations provided
     * in the environment file.
     * @return the datasource object corresponding to the configurations
     */
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