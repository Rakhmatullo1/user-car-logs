package com.rakhmatullo.usercarlogs.exception;


public class PermissionNotFoundException extends Exception {
    public PermissionNotFoundException(String message) {
        super(message);
    }

    public PermissionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionNotFoundException(Throwable cause) {
        super(cause);
    }
}
