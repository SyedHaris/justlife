-- liquibase formatted sql
-- changeset Haris:1720391319322-1

CREATE TABLE cleaning_professional (
   id BIGINT AUTO_INCREMENT NOT NULL,
   name VARCHAR(50) NOT NULL,
   email VARCHAR(255) NOT NULL,
   image_url VARCHAR(255) NULL,
   rating DOUBLE NULL,
   vehicle_id BIGINT NULL,
   created_date datetime NULL,
   last_modified_date datetime NULL,
   CONSTRAINT pk_cleaningprofessional PRIMARY KEY (id)
);

ALTER TABLE cleaning_professional ADD CONSTRAINT uc_cleaningprofessional_email UNIQUE (email);

ALTER TABLE cleaning_professional ADD CONSTRAINT FK_CLEANINGPROFESSIONAL_ON_VEHICLE FOREIGN KEY (vehicle_id) REFERENCES vehicle (id);