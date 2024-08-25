package com.example.BlogWebSite.exeption.exceptions;

/**
 * Exception that we get when in some logic we have bad ID.
 */
public class WrongIdException extends RuntimeException {
    public WrongIdException(String message) {
        super(message);
    }
}
