-- Type: status_enum

-- DROP TYPE IF EXISTS public.status_enum;

CREATE TYPE public.status_enum AS ENUM
    ('GLOBAL', 'CHECKPOINT', 'DIFF');

ALTER TYPE public.status_enum
    OWNER TO postgres;
