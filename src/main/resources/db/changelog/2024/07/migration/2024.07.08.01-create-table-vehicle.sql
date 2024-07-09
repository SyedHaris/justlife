-- liquibase formatted sql
-- changeset Haris:1720391334475-1

CREATE TABLE vehicle (
   id BIGINT AUTO_INCREMENT NOT NULL,
   number VARCHAR(20) NOT NULL,
   driver_name VARCHAR(100) NOT NULL,
   make VARCHAR(100) NOT NULL,
   model VARCHAR(100) NOT NULL,
   year VARCHAR(4) NOT NULL,
   created_date datetime NULL,
   last_modified_date datetime NULL,
   CONSTRAINT pk_vehicle PRIMARY KEY (id)
);

ALTER TABLE vehicle ADD CONSTRAINT uc_vehicle_number UNIQUE (number);