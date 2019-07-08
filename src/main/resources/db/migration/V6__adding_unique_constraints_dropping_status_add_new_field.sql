-- RDCC-230 Data Model changes
ALTER TABLE organisation ADD CONSTRAINT SRA_ID_UQ1 UNIQUE (SRA_ID);

ALTER TABLE professional_user DROP COLUMN status;

ALTER TABLE professional_user ADD COLUMN deleted timestamp;

ALTER TABLE professional_user ADD COLUMN deleted_flag boolean;

ALTER TABLE professional_user DROP CONSTRAINT EMAIL_ADDRESS_UQ1;

ALTER TABLE professional_user ADD CONSTRAINT EMAIL_ADDRESS_UQ1 UNIQUE (EMAIL_ADDRESS, DELETED_FLAG);

--RDCC-273 SIDAM roles format updated
UPDATE  PRD_ENUM SET ENUM_NAME = 'pui-user-manager' , ENUM_TYPE = 'SIDAM_ROLE' WHERE ENUM_CODE = 0;
UPDATE  PRD_ENUM SET ENUM_NAME = 'pui-organisation-manager' , ENUM_TYPE = 'SIDAM_ROLE' WHERE ENUM_CODE = 1;
UPDATE  PRD_ENUM SET ENUM_NAME = 'pui-finance-manager' , ENUM_TYPE = 'SIDAM_ROLE' WHERE ENUM_CODE = 2;
UPDATE  PRD_ENUM SET ENUM_NAME = 'pui-case-manager' , ENUM_TYPE = 'SIDAM_ROLE' WHERE ENUM_CODE = 3;
UPDATE  PRD_ENUM SET ENUM_NAME = 'organisation-admin' , ENUM_TYPE = 'ADMIN_ROLE' WHERE ENUM_CODE = 4;