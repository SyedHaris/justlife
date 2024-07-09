-- liquibase formatted sql
-- changeset Haris:1720549147772-1

ALTER TABLE booked_slot ADD COLUMN cp_id BIGINT NOT NULL AFTER booking_id;

ALTER TABLE booked_slot ADD CONSTRAINT FK_BOOKEDSLOT_ON_CLEANING_PROFESSIONAL FOREIGN KEY (cp_id) REFERENCES cleaning_professional (id);
