package com.example.BlogWebSite.exeption.exceptions;

/**
 * Exception that we get when in some logic we have bad password.
 */
public class WrongPasswordException extends RuntimeException{
    public WrongPasswordException(String message) {
        super(message);
    }
}
