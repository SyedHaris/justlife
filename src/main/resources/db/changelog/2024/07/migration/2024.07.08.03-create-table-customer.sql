-- liquibase formatted sql
-- changeset Haris:1720391357575-1

CREATE TABLE customer (
   id BIGINT AUTO_INCREMENT NOT NULL,
   name VARCHAR(100) NOT NULL,
   email VARCHAR(255) NOT NULL,
   phone VARCHAR(20) NOT NULL,
   address VARCHAR(100) NULL,
   image_url VARCHAR(255) NULL,
   created_date datetime NULL,
   last_modified_date datetime NULL,
   CONSTRAINT pk_customer PRIMARY KEY (id)
);

ALTER TABLE customer ADD CONSTRAINT uc_customer_email UNIQUE (email);

ALTER TABLE customer ADD CONSTRAINT uc_customer_phone UNIQUE (phone);