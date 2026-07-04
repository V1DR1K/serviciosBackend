CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE app_user (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), email VARCHAR(254) NOT NULL UNIQUE, password_hash VARCHAR(255),
 full_name VARCHAR(160), dni_encrypted TEXT, phone VARCHAR(40), photo_url TEXT, locality VARCHAR(120),
 latitude DOUBLE PRECISION, longitude DOUBLE PRECISION, location geography(Point,4326),
 onboarding_complete BOOLEAN NOT NULL DEFAULT FALSE, professional_enabled BOOLEAN NOT NULL DEFAULT FALSE,
 available BOOLEAN NOT NULL DEFAULT TRUE, rating NUMERIC(2,1) NOT NULL DEFAULT 0, rating_count INTEGER NOT NULL DEFAULT 0,
 deleted_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_user_location ON app_user USING gist(location);
CREATE TABLE trade (id BIGSERIAL PRIMARY KEY, code VARCHAR(60) UNIQUE NOT NULL, name VARCHAR(100) NOT NULL, icon VARCHAR(60) NOT NULL);
CREATE TABLE user_trade (user_id UUID REFERENCES app_user(id), trade_id BIGINT REFERENCES trade(id), PRIMARY KEY(user_id,trade_id));
CREATE TYPE request_status AS ENUM ('DRAFT','PUBLISHED','HAS_APPLICANTS','ASSIGNED','IN_PROGRESS','PENDING_CONFIRMATION','COMPLETED','CANCELLED');
CREATE TABLE service_request (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), client_id UUID NOT NULL REFERENCES app_user(id), trade_id BIGINT NOT NULL REFERENCES trade(id),
 title VARCHAR(140) NOT NULL, description VARCHAR(1600) NOT NULL, locality VARCHAR(120) NOT NULL,
 latitude DOUBLE PRECISION NOT NULL, longitude DOUBLE PRECISION NOT NULL, location geography(Point,4326) NOT NULL,
 urgency VARCHAR(20) NOT NULL DEFAULT 'NORMAL', budget NUMERIC(12,2), status request_status NOT NULL DEFAULT 'DRAFT',
 selected_application_id UUID, client_completed BOOLEAN NOT NULL DEFAULT FALSE, professional_completed BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now(), deleted_at TIMESTAMPTZ
);
CREATE INDEX idx_request_location ON service_request USING gist(location);
CREATE TABLE application (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), request_id UUID NOT NULL REFERENCES service_request(id), professional_id UUID NOT NULL REFERENCES app_user(id),
 status VARCHAR(20) NOT NULL DEFAULT 'PENDING', message VARCHAR(500), created_at TIMESTAMPTZ NOT NULL DEFAULT now(), UNIQUE(request_id,professional_id)
);
ALTER TABLE service_request ADD CONSTRAINT fk_selected_application FOREIGN KEY(selected_application_id) REFERENCES application(id);
CREATE TABLE rating (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), request_id UUID REFERENCES service_request(id), author_id UUID REFERENCES app_user(id), target_id UUID REFERENCES app_user(id), score SMALLINT CHECK(score BETWEEN 1 AND 5), comment VARCHAR(600), created_at TIMESTAMPTZ DEFAULT now(), UNIQUE(request_id,author_id));
CREATE TABLE report (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), reporter_id UUID REFERENCES app_user(id), reported_user_id UUID REFERENCES app_user(id), request_id UUID REFERENCES service_request(id), reason VARCHAR(60) NOT NULL, details VARCHAR(800), created_at TIMESTAMPTZ DEFAULT now(), UNIQUE(reporter_id,reported_user_id,request_id));
CREATE TABLE refresh_token (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID REFERENCES app_user(id), token_hash VARCHAR(64) UNIQUE NOT NULL, expires_at TIMESTAMPTZ NOT NULL, revoked_at TIMESTAMPTZ, created_at TIMESTAMPTZ DEFAULT now());
CREATE TABLE recovery_token (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID REFERENCES app_user(id), token_hash VARCHAR(64) UNIQUE NOT NULL, expires_at TIMESTAMPTZ NOT NULL, used_at TIMESTAMPTZ, created_at TIMESTAMPTZ DEFAULT now());
CREATE TABLE push_subscription (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID REFERENCES app_user(id), endpoint TEXT UNIQUE NOT NULL, p256dh TEXT NOT NULL, auth TEXT NOT NULL, created_at TIMESTAMPTZ DEFAULT now());
CREATE TABLE media_file (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), owner_id UUID REFERENCES app_user(id), request_id UUID REFERENCES service_request(id), object_key TEXT UNIQUE NOT NULL, content_type VARCHAR(100), size_bytes BIGINT, created_at TIMESTAMPTZ DEFAULT now());
INSERT INTO trade(code,name,icon) VALUES ('PLUMBING','Plomería','plumbing'),('ELECTRICITY','Electricidad','bolt'),('CLEANING','Limpieza','cleaning_services'),('GARDENING','Jardinería','yard'),('GAS','Gasista','gas_meter'),('CARPENTRY','Carpintería','carpenter'),('PAINTING','Pintura','format_paint'),('ROOFING','Techista','roofing');
