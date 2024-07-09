-- liquibase formatted sql
--changeset Haris:1720450544455-1

CREATE TABLE schedule_configuration (
   id BIGINT AUTO_INCREMENT NOT NULL,
   start_time time NOT NULL,
   end_time time NOT NULL,
   break_duration_minutes INT NOT NULL,
   holiday VARCHAR(255) NULL,
   created_date datetime NULL,
   last_modified_date datetime NULL,
   CONSTRAINT pk_scheduleconfiguration PRIMARY KEY (id)
);