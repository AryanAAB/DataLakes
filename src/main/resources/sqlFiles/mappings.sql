-- Table: public.mappings

-- DROP TABLE IF EXISTS public.mappings;

CREATE TABLE IF NOT EXISTS public.mappings
(
    mapping_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    file_id bigint NOT NULL,
    schema_id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT mappings_pkey PRIMARY KEY (mapping_id),
    CONSTRAINT fk_mappings_file FOREIGN KEY (file_id)
        REFERENCES public."FileMetaData" ("globalFileId") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_mappings_schema FOREIGN KEY (schema_id)
        REFERENCES public.schemas (schema_id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.mappings
    OWNER to postgres;