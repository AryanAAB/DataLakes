package org.example.bronze.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants
{
    private Constants() {}

    public static final Logger logger = LoggerFactory.getLogger("Bronze");

    public static final String HASH_ALGORHTM = "SHA-256";
    public static final int HASH_BUFFER_SIZE = 8192;

    public static final String xDeltaEncodeCommand = "xdelta3 -e -s";
    public static final String xDeltaDecodeCommand = "xdelta3 -d -s";
}
