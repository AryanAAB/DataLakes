package org.example.bronze.versioning.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class HashUtil
{
    public static String sha256(Path path) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance(VersioningConstants.HASH_ALGORHTM);

        try (InputStream is = Files.newInputStream(path))
        {
            byte[] buffer = new byte[VersioningConstants.HASH_BUFFER_SIZE];
            int read;

            while ((read = is.read(buffer)) != -1)
            {
                digest.update(buffer, 0, read);
            }
        }

        return bytesToHex(digest.digest());
    }

    private static String bytesToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}