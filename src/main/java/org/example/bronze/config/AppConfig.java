/**
 *  Utility class for loading and accessing application configuration properties.
 *  This class reads key-value pairs from an environment file located in the classpath.
 *  The properties are loaded once during class initialization and cached for subsequent access.
 *
 * @author Aryan
 * @author Abhirath
 * @author Kavya
 * @author Nainika
 * @version 1.0
 * @since 1.0
 */

package org.example.bronze.config;

import org.example.bronze.util.Constants;

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
                             .getResourceAsStream(Constants.ENVIRONMENT_FILE))
        {
            props.load(is);

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the value for the given key
     * @param key the key to search the value of
     * @return the corresponding value (or null if not found)
     */
    public static String get(String key)
    {
        return props.getProperty(key);
    }

    private AppConfig()
    {
    }
}
