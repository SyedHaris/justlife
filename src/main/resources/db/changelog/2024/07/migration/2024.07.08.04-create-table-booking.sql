-- liquibase formatted sql
-- changeset Haris:1720391369091-1

CREATE TABLE booking (
   id BIGINT AUTO_INCREMENT NOT NULL,
   date date NULL,
   start_time time NOT NULL,
   end_time time NOT NULL,
   status INT NOT NULL,
   customer_id BIGINT NULL,
   created_date datetime NULL,
   last_modified_date datetime NULL,
   CONSTRAINT pk_booking PRIMARY KEY (id)
);

ALTER TABLE booking ADD CONSTRAINT FK_BOOKING_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (id);