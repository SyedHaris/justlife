package com.example.justlife.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException() {
        super("Customer doesn't exist");
    }
}
