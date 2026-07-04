CREATE OR REPLACE FUNCTION sync_user_location() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN IF NEW.latitude IS NOT NULL AND NEW.longitude IS NOT NULL THEN NEW.location=ST_SetSRID(ST_MakePoint(NEW.longitude,NEW.latitude),4326)::geography; END IF; RETURN NEW; END $$;
CREATE TRIGGER trg_user_location BEFORE INSERT OR UPDATE OF latitude,longitude ON app_user FOR EACH ROW EXECUTE FUNCTION sync_user_location();
CREATE OR REPLACE FUNCTION sync_request_location() RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN NEW.location=ST_SetSRID(ST_MakePoint(NEW.longitude,NEW.latitude),4326)::geography; RETURN NEW; END $$;
CREATE TRIGGER trg_request_location BEFORE INSERT OR UPDATE OF latitude,longitude ON service_request FOR EACH ROW EXECUTE FUNCTION sync_request_location();
