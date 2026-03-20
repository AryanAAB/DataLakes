package org.example.bronze.metadata;

public enum FileType
{
    GLOBAL,
    CHECKPOINT,
    DIFF;

    public static FileType getVersionFile(boolean isCheckpoint)
    {
        if(isCheckpoint) return CHECKPOINT;
        else return DIFF;
    }
}
