-- liquibase formatted sql
-- changeset Haris:1720391405299-1

CREATE TABLE booked_slot (
   id BIGINT AUTO_INCREMENT NOT NULL,
   date date NOT NULL,
   start_time time NOT NULL,
   end_time time NOT NULL,
   created_date datetime NULL,
   last_modified_date datetime NULL,
   CONSTRAINT pk_bookedslot PRIMARY KEY (id)
);