CREATE OR REPLACE FUNCTION delete_mappings_on_file_update()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM public.mappings
    WHERE file_id = NEW."globalFileId";
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_file_update
AFTER UPDATE ON public."FileMetaData"
FOR EACH ROW
EXECUTE FUNCTION delete_mappings_on_file_update();