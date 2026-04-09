package org.example.bronze.util;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig
{
    private static final Properties props = new Properties();

    static
    {
        try (InputStream is =
                     AppConfig.class
                             .getClassLoader()
                             .getResourceAsStream("application.properties"))
        {
            props.load(is);

        } catch (Exception e)
        {
            Constants.logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static String get(String key)
    {
        return props.getProperty(key);
    }

    private AppConfig()
    {
    }
}