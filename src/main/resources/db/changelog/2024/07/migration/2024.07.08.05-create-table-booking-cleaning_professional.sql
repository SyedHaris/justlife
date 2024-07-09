-- liquibase formatted sql
-- changeset Haris:1720391379915-1

CREATE TABLE booking_cleaning_professional (
   booking_id BIGINT NOT NULL,
   cp_id BIGINT NOT NULL,
   CONSTRAINT pk_booking_cleaning_professional PRIMARY KEY (booking_id, cp_id)
);

ALTER TABLE booking_cleaning_professional ADD CONSTRAINT fk_booclepro_on_booking FOREIGN KEY (booking_id) REFERENCES booking (id);

ALTER TABLE booking_cleaning_professional ADD CONSTRAINT fk_booclepro_on_cleaning_professional FOREIGN KEY (cp_id) REFERENCES cleaning_professional (id);