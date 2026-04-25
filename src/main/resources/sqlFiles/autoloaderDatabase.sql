--
-- PostgreSQL database dump
--

\restrict ZhlP3PIDwP6JElHZT4iI63PiBK4WLM44P60ZnU31AdJZf7RpC5hfP2hEUwR3xOr

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.3

-- Started on 2026-04-25 11:30:57

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5112 (class 1262 OID 16551)
-- Name: autoloader; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE autoloader WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';


ALTER DATABASE autoloader OWNER TO postgres;

\unrestrict ZhlP3PIDwP6JElHZT4iI63PiBK4WLM44P60ZnU31AdJZf7RpC5hfP2hEUwR3xOr
\connect autoloader
\restrict ZhlP3PIDwP6JElHZT4iI63PiBK4WLM44P60ZnU31AdJZf7RpC5hfP2hEUwR3xOr

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 882 (class 1247 OID 16766)
-- Name: status_enum; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.status_enum AS ENUM (
    'GLOBAL',
    'CHECKPOINT',
    'DIFF'
);


ALTER TYPE public.status_enum OWNER TO postgres;

--
-- TOC entry 236 (class 1255 OID 16895)
-- Name: deactivate_user_pipelines(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.deactivate_user_pipelines() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Only act on transition: TRUE → FALSE
    IF OLD.isActive = TRUE AND NEW.isActive = FALSE THEN
        UPDATE pipeline
        SET isActive = FALSE
        WHERE user_id = NEW.user_id;
    END IF;

    RETURN NEW;
END;
$$;


ALTER FUNCTION public.deactivate_user_pipelines() OWNER TO postgres;

--
-- TOC entry 237 (class 1255 OID 16763)
-- Name: delete_mappings_on_file_update(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_mappings_on_file_update() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM public.mappings
    WHERE file_id = NEW."globalFileId";
    
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.delete_mappings_on_file_update() OWNER TO postgres;

--
-- TOC entry 235 (class 1255 OID 16761)
-- Name: delete_mappings_on_schema_update(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_mappings_on_schema_update() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM public.mappings
    WHERE schema_id = NEW.schema_id;
    
    RETURN NEW;
END;
$$;


ALTER FUNCTION public.delete_mappings_on_schema_update() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 222 (class 1259 OID 16686)
-- Name: FileMetaData; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."FileMetaData" (
    "globalFileId" bigint NOT NULL,
    "pipelineId" bigint NOT NULL,
    id text NOT NULL,
    "parentId" text,
    name text NOT NULL,
    "mimeType" text NOT NULL,
    "exportMimeType" text,
    size bigint,
    path text,
    "createdTime" timestamp with time zone NOT NULL,
    "modifiedTime" timestamp with time zone NOT NULL
);


ALTER TABLE public."FileMetaData" OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16685)
-- Name: FileMetaData_globalFileId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public."FileMetaData" ALTER COLUMN "globalFileId" ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public."FileMetaData_globalFileId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 220 (class 1259 OID 16594)
-- Name: Pipeline; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Pipeline" (
    "pipelineId" bigint CONSTRAINT pipeline_pipelineid_not_null NOT NULL,
    "isActive" boolean CONSTRAINT pipeline_isactive_not_null NOT NULL,
    "configFilePath" text CONSTRAINT pipeline_configfilepath_not_null NOT NULL,
    "createdAt" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    category_id bigint NOT NULL,
    user_id bigint
);


ALTER TABLE public."Pipeline" OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 16851)
-- Name: category_tags; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category_tags (
    category_id bigint NOT NULL,
    tag_id bigint NOT NULL
);


ALTER TABLE public.category_tags OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16806)
-- Name: file_objects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.file_objects (
    file_object_id bigint NOT NULL,
    file_id bigint NOT NULL,
    file_type public.status_enum NOT NULL,
    version integer,
    base_version integer,
    file_path text NOT NULL,
    hash text NOT NULL,
    size bigint NOT NULL,
    CONSTRAINT chk_file_type_version CHECK ((((file_type = 'GLOBAL'::public.status_enum) AND (version IS NULL)) OR ((file_type <> 'GLOBAL'::public.status_enum) AND (version IS NOT NULL))))
);


ALTER TABLE public.file_objects OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16805)
-- Name: file_objects_file_object_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.file_objects ALTER COLUMN file_object_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.file_objects_file_object_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 225 (class 1259 OID 16724)
-- Name: mappings; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mappings (
    mapping_id bigint NOT NULL,
    file_id bigint NOT NULL,
    schema_id character varying(255) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.mappings OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16723)
-- Name: mappings_mapping_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.mappings ALTER COLUMN mapping_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.mappings_mapping_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 229 (class 1259 OID 16828)
-- Name: pipeline_categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pipeline_categories (
    category_id bigint NOT NULL,
    category_name text NOT NULL
);


ALTER TABLE public.pipeline_categories OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 16827)
-- Name: pipeline_categories_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.pipeline_categories ALTER COLUMN category_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.pipeline_categories_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 219 (class 1259 OID 16593)
-- Name: pipeline_pipelineid_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public."Pipeline" ALTER COLUMN "pipelineId" ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.pipeline_pipelineid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 223 (class 1259 OID 16710)
-- Name: schemas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schemas (
    schema_id text NOT NULL,
    schema_applicable_type text NOT NULL,
    schema_custom_validator_path text,
    schema_file_path text NOT NULL
);


ALTER TABLE public.schemas OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16840)
-- Name: tags; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.tags (
    tag_id bigint NOT NULL,
    tag_name text NOT NULL
);


ALTER TABLE public.tags OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 16839)
-- Name: tags_tag_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.tags ALTER COLUMN tag_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.tags_tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 234 (class 1259 OID 16877)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id bigint NOT NULL,
    internal_id text NOT NULL,
    name text NOT NULL,
    isactive boolean NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 16876)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

ALTER TABLE public.users ALTER COLUMN user_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- TOC entry 5094 (class 0 OID 16686)
-- Dependencies: 222
-- Data for Name: FileMetaData; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."FileMetaData" ("globalFileId", "pipelineId", id, "parentId", name, "mimeType", "exportMimeType", size, path, "createdTime", "modifiedTime") FROM stdin;
\.


--
-- TOC entry 5092 (class 0 OID 16594)
-- Dependencies: 220
-- Data for Name: Pipeline; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Pipeline" ("pipelineId", "isActive", "configFilePath", "createdAt", category_id, user_id) FROM stdin;
\.


--
-- TOC entry 5104 (class 0 OID 16851)
-- Dependencies: 232
-- Data for Name: category_tags; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.category_tags (category_id, tag_id) FROM stdin;
\.


--
-- TOC entry 5099 (class 0 OID 16806)
-- Dependencies: 227
-- Data for Name: file_objects; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.file_objects (file_object_id, file_id, file_type, version, base_version, file_path, hash, size) FROM stdin;
\.


--
-- TOC entry 5097 (class 0 OID 16724)
-- Dependencies: 225
-- Data for Name: mappings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.mappings (mapping_id, file_id, schema_id, created_at) FROM stdin;
\.


--
-- TOC entry 5101 (class 0 OID 16828)
-- Dependencies: 229
-- Data for Name: pipeline_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pipeline_categories (category_id, category_name) FROM stdin;
\.


--
-- TOC entry 5095 (class 0 OID 16710)
-- Dependencies: 223
-- Data for Name: schemas; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.schemas (schema_id, schema_applicable_type, schema_custom_validator_path, schema_file_path) FROM stdin;
\.


--
-- TOC entry 5103 (class 0 OID 16840)
-- Dependencies: 231
-- Data for Name: tags; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tags (tag_id, tag_name) FROM stdin;
\.


--
-- TOC entry 5106 (class 0 OID 16877)
-- Dependencies: 234
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, internal_id, name, isactive) FROM stdin;
\.


--
-- TOC entry 5113 (class 0 OID 0)
-- Dependencies: 221
-- Name: FileMetaData_globalFileId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."FileMetaData_globalFileId_seq"', 364, true);


--
-- TOC entry 5114 (class 0 OID 0)
-- Dependencies: 226
-- Name: file_objects_file_object_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.file_objects_file_object_id_seq', 120, true);


--
-- TOC entry 5115 (class 0 OID 0)
-- Dependencies: 224
-- Name: mappings_mapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mappings_mapping_id_seq', 46, true);


--
-- TOC entry 5116 (class 0 OID 0)
-- Dependencies: 228
-- Name: pipeline_categories_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pipeline_categories_category_id_seq', 1, false);


--
-- TOC entry 5117 (class 0 OID 0)
-- Dependencies: 219
-- Name: pipeline_pipelineid_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.pipeline_pipelineid_seq', 2, true);


--
-- TOC entry 5118 (class 0 OID 0)
-- Dependencies: 230
-- Name: tags_tag_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tags_tag_id_seq', 1, false);


--
-- TOC entry 5119 (class 0 OID 0)
-- Dependencies: 233
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 1, false);


--
-- TOC entry 4906 (class 2606 OID 16701)
-- Name: FileMetaData FileMetaData_path_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."FileMetaData"
    ADD CONSTRAINT "FileMetaData_path_key" UNIQUE (path);


--
-- TOC entry 4908 (class 2606 OID 16699)
-- Name: FileMetaData FileMetaData_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."FileMetaData"
    ADD CONSTRAINT "FileMetaData_pkey" PRIMARY KEY ("globalFileId");


--
-- TOC entry 4928 (class 2606 OID 16857)
-- Name: category_tags category_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT category_tags_pkey PRIMARY KEY (category_id, tag_id);


--
-- TOC entry 4916 (class 2606 OID 16819)
-- Name: file_objects file_objects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file_objects
    ADD CONSTRAINT file_objects_pkey PRIMARY KEY (file_object_id);


--
-- TOC entry 4914 (class 2606 OID 16755)
-- Name: mappings mappings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mappings
    ADD CONSTRAINT mappings_pkey PRIMARY KEY (mapping_id);


--
-- TOC entry 4920 (class 2606 OID 16838)
-- Name: pipeline_categories pipeline_categories_category_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pipeline_categories
    ADD CONSTRAINT pipeline_categories_category_name_key UNIQUE (category_name);


--
-- TOC entry 4922 (class 2606 OID 16836)
-- Name: pipeline_categories pipeline_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pipeline_categories
    ADD CONSTRAINT pipeline_categories_pkey PRIMARY KEY (category_id);


--
-- TOC entry 4904 (class 2606 OID 16604)
-- Name: Pipeline pipeline_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Pipeline"
    ADD CONSTRAINT pipeline_pkey PRIMARY KEY ("pipelineId");


--
-- TOC entry 4912 (class 2606 OID 16745)
-- Name: schemas schemas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schemas
    ADD CONSTRAINT schemas_pkey PRIMARY KEY (schema_id);


--
-- TOC entry 4924 (class 2606 OID 16848)
-- Name: tags tags_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_pkey PRIMARY KEY (tag_id);


--
-- TOC entry 4926 (class 2606 OID 16850)
-- Name: tags tags_tag_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_tag_name_key UNIQUE (tag_name);


--
-- TOC entry 4910 (class 2606 OID 16703)
-- Name: FileMetaData unique_pipeline_file; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."FileMetaData"
    ADD CONSTRAINT unique_pipeline_file UNIQUE ("pipelineId", id);


--
-- TOC entry 4930 (class 2606 OID 16889)
-- Name: users users_internal_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_internal_id_key UNIQUE (internal_id);


--
-- TOC entry 4932 (class 2606 OID 16887)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4917 (class 1259 OID 16825)
-- Name: ux_file_objects_global; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX ux_file_objects_global ON public.file_objects USING btree (file_id) WHERE (file_type = 'GLOBAL'::public.status_enum);


--
-- TOC entry 4918 (class 1259 OID 16826)
-- Name: ux_file_objects_non_global; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX ux_file_objects_non_global ON public.file_objects USING btree (file_id, version) WHERE (file_type <> 'GLOBAL'::public.status_enum);


--
-- TOC entry 4943 (class 2620 OID 16896)
-- Name: users trg_deactivate_user_pipelines; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_deactivate_user_pipelines AFTER UPDATE OF isactive ON public.users FOR EACH ROW EXECUTE FUNCTION public.deactivate_user_pipelines();


--
-- TOC entry 4941 (class 2620 OID 16764)
-- Name: FileMetaData trg_file_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_file_update AFTER UPDATE ON public."FileMetaData" FOR EACH ROW EXECUTE FUNCTION public.delete_mappings_on_file_update();


--
-- TOC entry 4942 (class 2620 OID 16762)
-- Name: schemas trg_schema_update; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trg_schema_update AFTER UPDATE ON public.schemas FOR EACH ROW EXECUTE FUNCTION public.delete_mappings_on_schema_update();


--
-- TOC entry 4939 (class 2606 OID 16858)
-- Name: category_tags category_tags_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT category_tags_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.pipeline_categories(category_id) ON DELETE CASCADE;


--
-- TOC entry 4940 (class 2606 OID 16863)
-- Name: category_tags category_tags_tag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category_tags
    ADD CONSTRAINT category_tags_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES public.tags(tag_id) ON DELETE CASCADE;


--
-- TOC entry 4938 (class 2606 OID 16820)
-- Name: file_objects fk_file_objects_file; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file_objects
    ADD CONSTRAINT fk_file_objects_file FOREIGN KEY (file_id) REFERENCES public."FileMetaData"("globalFileId") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 4936 (class 2606 OID 16739)
-- Name: mappings fk_mappings_file; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mappings
    ADD CONSTRAINT fk_mappings_file FOREIGN KEY (file_id) REFERENCES public."FileMetaData"("globalFileId") ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 4937 (class 2606 OID 16747)
-- Name: mappings fk_mappings_schema; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mappings
    ADD CONSTRAINT fk_mappings_schema FOREIGN KEY (schema_id) REFERENCES public.schemas(schema_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 4935 (class 2606 OID 16704)
-- Name: FileMetaData fk_metadata_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."FileMetaData"
    ADD CONSTRAINT fk_metadata_parent FOREIGN KEY ("pipelineId") REFERENCES public."Pipeline"("pipelineId");


--
-- TOC entry 4933 (class 2606 OID 16871)
-- Name: Pipeline fk_pipeline_category; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Pipeline"
    ADD CONSTRAINT fk_pipeline_category FOREIGN KEY (category_id) REFERENCES public.pipeline_categories(category_id) ON DELETE RESTRICT;


--
-- TOC entry 4934 (class 2606 OID 16890)
-- Name: Pipeline fk_pipeline_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Pipeline"
    ADD CONSTRAINT fk_pipeline_user FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE RESTRICT;


-- Completed on 2026-04-25 11:30:57

--
-- PostgreSQL database dump complete
--

\unrestrict ZhlP3PIDwP6JElHZT4iI63PiBK4WLM44P60ZnU31AdJZf7RpC5hfP2hEUwR3xOr

