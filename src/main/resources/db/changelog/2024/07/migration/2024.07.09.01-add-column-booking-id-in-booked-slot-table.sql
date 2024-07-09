-- liquibase formatted sql
-- changeset Haris:1720524542919-1

ALTER TABLE booked_slot add column booking_id BIGINT NOT NULL AFTER end_time;

ALTER TABLE booked_slot ADD CONSTRAINT FK_BOOKEDSLOT_ON_BOOKING FOREIGN KEY (booking_id) REFERENCES booking (id);