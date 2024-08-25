package com.example.BlogWebSite.exeption.exceptions;


/**
 * Exception that we get when user trying to pass bad request.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
