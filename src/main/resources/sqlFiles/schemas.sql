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