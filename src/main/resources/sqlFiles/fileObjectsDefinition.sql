-- Table: public.file_objects

-- DROP TABLE IF EXISTS public.file_objects;

CREATE TABLE IF NOT EXISTS public.file_objects
(
    file_object_id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    file_id bigint NOT NULL,
    file_type status_enum NOT NULL,
    version integer,
    base_version integer,
    file_path text COLLATE pg_catalog."default" NOT NULL,
    hash text COLLATE pg_catalog."default" NOT NULL,
    size bigint NOT NULL,
    CONSTRAINT file_objects_pkey PRIMARY KEY (file_object_id),
    CONSTRAINT fk_file_objects_file FOREIGN KEY (file_id)
    REFERENCES public."FileMetaData" ("globalFileId") MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE,
    CONSTRAINT chk_file_type_version CHECK (file_type = 'GLOBAL'::status_enum AND version IS NULL OR file_type <> 'GLOBAL'::status_enum AND version IS NOT NULL)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.file_objects
    OWNER to postgres;
-- Index: ux_file_objects_global

-- DROP INDEX IF EXISTS public.ux_file_objects_global;

CREATE UNIQUE INDEX IF NOT EXISTS ux_file_objects_global
    ON public.file_objects USING btree
    (file_id ASC NULLS LAST)
    WITH (fillfactor=100, deduplicate_items=True)
    TABLESPACE pg_default
    WHERE file_type = 'GLOBAL'::status_enum;
-- Index: ux_file_objects_non_global

-- DROP INDEX IF EXISTS public.ux_file_objects_non_global;

CREATE UNIQUE INDEX IF NOT EXISTS ux_file_objects_non_global
    ON public.file_objects USING btree
    (file_id ASC NULLS LAST, version ASC NULLS LAST)
    WITH (fillfactor=100, deduplicate_items=True)
    TABLESPACE pg_default
    WHERE file_type <> 'GLOBAL'::status_enum;