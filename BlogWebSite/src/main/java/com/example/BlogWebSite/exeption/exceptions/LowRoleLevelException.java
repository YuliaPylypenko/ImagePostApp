package com.example.BlogWebSite.exeption.exceptions;


/**
 * Exception that we get when user trying to update user status.
 */
public class LowRoleLevelException extends RuntimeException {
    public LowRoleLevelException(String message) {
        super(message);
    }
}
