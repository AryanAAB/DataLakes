CREATE OR REPLACE FUNCTION delete_mappings_on_schema_update()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM public.mappings
    WHERE schema_id = NEW.schema_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_schema_update
AFTER UPDATE ON public.schemas
FOR EACH ROW
EXECUTE FUNCTION delete_mappings_on_schema_update();