-- liquibase formatted sql
-- changeset Haris:1720553659495-1

ALTER TABLE booking MODIFY COLUMN status VARCHAR(255) NOT NULL;