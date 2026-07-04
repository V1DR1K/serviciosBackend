ALTER TABLE service_request ALTER COLUMN status DROP DEFAULT;
ALTER TABLE service_request ALTER COLUMN status TYPE VARCHAR(32) USING status::text;
ALTER TABLE service_request ALTER COLUMN status SET DEFAULT 'DRAFT';
