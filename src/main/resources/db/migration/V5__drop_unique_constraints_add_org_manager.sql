ALTER TABLE organisation DROP CONSTRAINT company_url_uq1;

ALTER TABLE organisation DROP CONSTRAINT sra_id_uq1;

INSERT INTO PRD_ENUM (ENUM_CODE, ENUM_NAME, ENUM_TYPE, ENUM_DESC) VALUES (4, 'ORGANISATION_ADMIN', 'PRD_ROLE', 'Identifying the first user of an organisation');