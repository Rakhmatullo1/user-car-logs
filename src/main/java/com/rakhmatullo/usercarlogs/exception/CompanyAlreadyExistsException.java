package com.rakhmatullo.usercarlogs.exception;

public class CompanyAlreadyExistsException extends Exception {
    public CompanyAlreadyExistsException(String message) {
        super(message);
    }

    public CompanyAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompanyAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
