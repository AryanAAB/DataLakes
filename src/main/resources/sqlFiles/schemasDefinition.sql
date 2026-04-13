-- Table: public.schemas

-- DROP TABLE IF EXISTS public.schemas;

CREATE TABLE IF NOT EXISTS public.schemas
(
    schema_id text COLLATE pg_catalog."default" NOT NULL,
    schema_applicable_type text COLLATE pg_catalog."default" NOT NULL,
    schema_custom_validator_path text COLLATE pg_catalog."default",
    schema_file_path text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT schemas_pkey PRIMARY KEY (schema_id)
    )

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.schemas
    OWNER to postgres;

-- Trigger: trg_schema_update

-- DROP TRIGGER IF EXISTS trg_schema_update ON public.schemas;

CREATE OR REPLACE TRIGGER trg_schema_update
    AFTER UPDATE
                     ON public.schemas
                     FOR EACH ROW
                     EXECUTE FUNCTION public.delete_mappings_on_schema_update();