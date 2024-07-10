package com.example.justlife.exception;

public class HolidayException extends RuntimeException {
    public HolidayException() {
        super("Booking can't be created on a holiday");
    }
}
