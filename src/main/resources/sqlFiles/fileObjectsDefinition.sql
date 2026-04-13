-- Table: public.file_objects

-- DROP TABLE IF EXISTS public.file_objects;

CREATE TABLE IF NOT EXISTS public.file_objects
(
    file_id bigint NOT NULL,
    file_type status_enum NOT NULL,
    version integer NOT NULL,
    base_version integer,
    file_path text COLLATE pg_catalog."default" NOT NULL,
    hash text COLLATE pg_catalog."default" NOT NULL,
    size bigint NOT NULL,
    CONSTRAINT file_objects_pkey PRIMARY KEY (file_id, version),
    CONSTRAINT fk_file_objects_file FOREIGN KEY (file_id)
        REFERENCES public."FileMetaData" ("globalFileId") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.file_objects
    OWNER to postgres;