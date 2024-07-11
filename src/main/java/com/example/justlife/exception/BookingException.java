package com.example.justlife.exception;

public class BookingException extends RuntimeException {
    public static final String CLEANING_PROFESSIONALS_INVALID = "Provided cleaning professionals are not valid for this booking";
    public static final String SLOT_NOT_AVAILABLE = "Slot not available";
    public static final String TIME_SLOT_NOT_VALID = "Provided time slot is invalid";

    public BookingException(String message) {
        super(message);
    }

    public BookingException() {
        super("Booking doesn't exist");
    }
}
