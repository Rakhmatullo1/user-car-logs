package com.rakhmatullo.usercarlogs.exception;

public class CarNotFoundException extends Exception {
    public CarNotFoundException(String message) {
        super(message);
    }

    public CarNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CarNotFoundException(Throwable cause) {
        super(cause);
    }
}
