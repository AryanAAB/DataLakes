-- Index: ux_file_objects_global

-- DROP INDEX IF EXISTS public.ux_file_objects_global;

CREATE UNIQUE INDEX IF NOT EXISTS ux_file_objects_global
    ON public.file_objects USING btree
    (file_id ASC NULLS LAST)
    WITH (fillfactor=100, deduplicate_items=True)
    TABLESPACE pg_default
    WHERE file_type = 'GLOBAL'::status_enum;

