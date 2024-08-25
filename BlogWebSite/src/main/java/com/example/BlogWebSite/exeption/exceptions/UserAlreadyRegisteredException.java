package com.example.BlogWebSite.exeption.exceptions;
/**
 * Exception that we get when user trying to sign-up with email that already
 * registered.
 */
public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}
