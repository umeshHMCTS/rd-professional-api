--RDCC-119
ALTER TABLE PROFESSIONAL_USER DROP COLUMN USER_IDENTIFIER;

ALTER TABLE PROFESSIONAL_USER ADD COLUMN USER_IDENTIFIER uuid;

ALTER TABLE PROFESSIONAL_USER ADD CONSTRAINT USER_IDENTIFIER_UQ UNIQUE (USER_IDENTIFIER);
