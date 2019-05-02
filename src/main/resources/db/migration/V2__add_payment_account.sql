CREATE TABLE PAYMENT_ACCOUNT(
	ID uuid NOT NULL,
	PBA_NUMBER varchar(255) NOT NULL,
	ORGANISATION_ID uuid NOT NULL,
	LAST_UPDATED timestamp NOT NULL,
	CREATED timestamp NOT NULL,
	CONSTRAINT PBA_NUMBER_UQ UNIQUE (PBA_NUMBER),
	CONSTRAINT PAYMENT_ACCOUNT_PK PRIMARY KEY (ID)
);

CREATE TABLE CONTACT_INFORMATION(
	ID uuid NOT NULL,
	ADDRESS_LINE1 varchar(150) NOT NULL,
	ADDRESS_LINE2 varchar(50),
	ADDRESS_LINE3 varchar(50),
	TOWN_CITY varchar(50),
	COUNTY varchar(50),
	POSTCODE varchar(14),
	COUNTRY varchar(50),
	ORGANISATION_ID uuid NOT NULL,
	LAST_UPDATED    TIMESTAMP NOT NULL,
    CREATED         TIMESTAMP NOT NULL,
	CONSTRAINT CONTACT_INFORMATION_PK PRIMARY KEY (ID)
);

CREATE TABLE DX_ADDRESS(
	ID uuid,
	DX_EXCHANGE varchar(20) NOT NULL,
	DX_NUMBER varchar(13) NOT NULL,
	CONTACT_INFORMATION_ID uuid NOT NULL,
	LAST_UPDATED timestamp NOT NULL,
	CREATED timestamp NOT NULL,
	CONSTRAINT DX_ADDRESS_PK PRIMARY KEY (ID)
);

CREATE TABLE USER_ATTRIBUTE(
	PROFESSIONAL_USER_ID uuid,
	PRD_ENUM_CODE smallint,
	PRD_ENUM_TYPE varchar(50),
	LAST_UPDATED    TIMESTAMP NOT NULL,
    CREATED         TIMESTAMP NOT NULL,
	CONSTRAINT PROFESSIONAL_USER_PK PRIMARY KEY (PROFESSIONAL_USER_ID,PRD_ENUM_CODE,PRD_ENUM_TYPE)
);

CREATE TABLE PRD_ENUM(
	ENUM_CODE smallint NOT NULL,
	ENUM_NAME varchar(50) NOT NULL,
	ENUM_TYPE varchar(50) NOT NULL,
	ENUM_DESC varchar(1024),
	CONSTRAINT PRD_ENUM_UQ PRIMARY KEY (ENUM_CODE,ENUM_TYPE)
);

CREATE TABLE USER_ACCOUNT_MAP(
	PROFESSIONAL_USER_ID uuid,
	PAYMENT_ACCOUNT_ID uuid,
	DEFAULTED boolean NOT NULL DEFAULT FALSE,
	CONSTRAINT PBA_MAPPING_PK PRIMARY KEY (PROFESSIONAL_USER_ID,PAYMENT_ACCOUNT_ID)

);

CREATE TABLE USER_ADDRESS_MAP(
	PROFESSIONAL_USER_ID uuid,
	CONTACT_ADDRESS_ID uuid,
	DEFAULTED boolean NOT NULL,
	CONSTRAINT ADDRESS_MAPPING_PK PRIMARY KEY (PROFESSIONAL_USER_ID,CONTACT_ADDRESS_ID)

);

CREATE TABLE DOMAIN(
    ID uuid,
    ORGANISATION_ID uuid NOT NULL,
    DOMAIN_IDENTIFIER uuid NOT NULL,
    NAME varchar(50) NOT NULL,
    LAST_UPDATED timestamp NOT NULL,
    CREATED timestamp NOT NULL,
    CONSTRAINT DOMAIN_PK PRIMARY KEY (ID),
    CONSTRAINT DONAIN_IDENTIFIER_UQ UNIQUE (DOMAIN_IDENTIFIER));

ALTER TABLE organisation
  ADD COLUMN SRA_ID VARCHAR(255) UNIQUE;

ALTER TABLE organisation
  ADD COLUMN SRA_REGULATED BOOLEAN;

ALTER TABLE organisation
  ADD COLUMN COMPANY_NUMBER VARCHAR(255) UNIQUE;

ALTER TABLE organisation
  ADD COLUMN COMPANY_URL VARCHAR(512) UNIQUE;

ALTER TABLE organisation
  ADD COLUMN ORGANISATION_IDENTIFIER UUID UNIQUE;

ALTER TABLE organisation ALTER COLUMN status SET DEFAULT 'PENDING';

ALTER TABLE PROFESSIONAL_USER ALTER COLUMN STATUS SET DEFAULT 'PENDING';
					    
ALTER TABLE PROFESSIONAL_USER ADD CONSTRAINT ORGANISATION_FK1 FOREIGN KEY (ORGANISATION_ID)
REFERENCES ORGANISATION (ID);

ALTER TABLE PAYMENT_ACCOUNT ADD CONSTRAINT ORGANISATION_FK2 FOREIGN KEY (ORGANISATION_ID)
REFERENCES ORGANISATION (ID);

ALTER TABLE CONTACT_INFORMATION ADD CONSTRAINT ORGANISATION_FK3 FOREIGN KEY (ORGANISATION_ID)
REFERENCES ORGANISATION (ID);

ALTER TABLE DX_ADDRESS ADD CONSTRAINT ORGANISATION_FK4 FOREIGN KEY (CONTACT_INFORMATION_ID)
REFERENCES CONTACT_INFORMATION (ID);

ALTER TABLE USER_ATTRIBUTE ADD CONSTRAINT PROFESSIONAL_USER_FK1 FOREIGN KEY (PROFESSIONAL_USER_ID)
REFERENCES PROFESSIONAL_USER (ID);

ALTER TABLE USER_ATTRIBUTE ADD CONSTRAINT PRD_ENUM_FK1 FOREIGN KEY (PRD_ENUM_CODE,PRD_ENUM_TYPE)
REFERENCES PRD_ENUM (ENUM_CODE,ENUM_TYPE);

ALTER TABLE USER_ACCOUNT_MAP ADD CONSTRAINT PBA_FK FOREIGN KEY (PAYMENT_ACCOUNT_ID)
REFERENCES PAYMENT_ACCOUNT (ID);

ALTER TABLE USER_ACCOUNT_MAP ADD CONSTRAINT PROFESSIONAL_USER_FK4 FOREIGN KEY (PROFESSIONAL_USER_ID)
REFERENCES PROFESSIONAL_USER (ID);

ALTER TABLE USER_ADDRESS_MAP ADD CONSTRAINT PROFESSIONAL_USER_FK5 FOREIGN KEY (PROFESSIONAL_USER_ID)
REFERENCES PROFESSIONAL_USER (ID);

ALTER TABLE USER_ADDRESS_MAP ADD CONSTRAINT CONTACT_ADDRESS_FK1 FOREIGN KEY (CONTACT_ADDRESS_ID)
REFERENCES CONTACT_INFORMATION (ID);
