-- Table: public.Pipeline

-- DROP TABLE IF EXISTS public."Pipeline";

CREATE TABLE IF NOT EXISTS public."Pipeline"
(
    "pipelineId" bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    "isActive" boolean NOT NULL,
    "configFilePath" text COLLATE pg_catalog."default" NOT NULL,
    "createdAt" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pipeline_pkey PRIMARY KEY ("pipelineId")
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public."Pipeline"
    OWNER to postgres;