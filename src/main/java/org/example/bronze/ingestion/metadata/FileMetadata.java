package org.example.bronze.ingestion.metadata;

import com.google.api.client.util.DateTime;

import java.time.Instant;

public class FileMetadata
{
    private String id, parentId, name, path, mimeType, exportMimeType;
    private Long size;
    private Instant createdTime, modifiedTime;

    public FileMetadata()
    {
    }

    public FileMetadata setId(String id)
    {
        this.id = id;
        return this;
    }

    public FileMetadata setParentId(String parentId)
    {
        this.parentId = parentId;
        return this;
    }

    public FileMetadata setName(String name)
    {
        this.name = name;
        return this;
    }

    public FileMetadata setPath(String path)
    {
        this.path = path;
        return this;
    }

    public FileMetadata setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
        return this;
    }

    public FileMetadata setExportMimeType(String exportMimeType)
    {
        this.exportMimeType = exportMimeType;
        return this;
    }

    public FileMetadata setSize(Long size)
    {
        this.size = size;
        return this;
    }

    public FileMetadata setCreatedTime(Instant createdTime)
    {
        this.createdTime = createdTime;
        return this;
    }

    public FileMetadata setModifiedTime(Instant modifiedTime)
    {
        this.modifiedTime = modifiedTime;
        return this;
    }

    public String getId()
    {
        return id;
    }

    public String getParentId()
    {
        return parentId;
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public String getExportMimeType()
    {
        return exportMimeType;
    }

    public Long getSize()
    {
        return size;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    @Override
    public String toString()
    {
        return "[id=" + id + ", name=" + name + ", parentId=" + parentId +
                ", path=" + path + ", mimeType=" + mimeType
                + ", exportMimeType=" + exportMimeType + ", size=" + size
                + ", createdTime=" + createdTime + ", modifiedTime=" + modifiedTime + "]";
    }
}