package org.example.bronze.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants
{
    public static final Logger logger = LoggerFactory.getLogger("BronzeIngestion");
    public static final String PIPELINE_STAGING_DIRECTORY = "src/main/resources/pipeline/stage/";
    public static final String VERSION_STAGING_DIRECTORY = "src/main/resources/version/";
}