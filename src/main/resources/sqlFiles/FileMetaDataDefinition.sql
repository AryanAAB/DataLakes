-- Table: public.FileMetaData

-- DROP TABLE IF EXISTS public."FileMetaData";

CREATE TABLE IF NOT EXISTS public."FileMetaData"
(
    "globalFileId" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "pipelineId" bigint NOT NULL,
    id text COLLATE pg_catalog."default" NOT NULL,
    "parentId" text COLLATE pg_catalog."default",
    name text COLLATE pg_catalog."default" NOT NULL,
    "mimeType" text COLLATE pg_catalog."default" NOT NULL,
    "exportMimeType" text COLLATE pg_catalog."default",
    size bigint,
    path text COLLATE pg_catalog."default",
    "createdTime" timestamp with time zone NOT NULL,
    "modifiedTime" timestamp with time zone NOT NULL,
    CONSTRAINT "FileMetaData_pkey" PRIMARY KEY ("globalFileId"),
    CONSTRAINT "FileMetaData_path_key" UNIQUE (path),
    CONSTRAINT unique_pipeline_file UNIQUE ("pipelineId", id),
    CONSTRAINT fk_metadata_parent FOREIGN KEY ("pipelineId")
        REFERENCES public."Pipeline" ("pipelineId") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public."FileMetaData"
    OWNER to postgres;