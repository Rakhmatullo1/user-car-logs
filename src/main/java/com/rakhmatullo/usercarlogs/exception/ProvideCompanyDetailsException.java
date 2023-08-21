package com.rakhmatullo.usercarlogs.exception;

public class ProvideCompanyDetailsException extends Exception {
    public ProvideCompanyDetailsException(String message) {
        super(message);
    }

    public ProvideCompanyDetailsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProvideCompanyDetailsException(Throwable cause) {
        super(cause);
    }
}
