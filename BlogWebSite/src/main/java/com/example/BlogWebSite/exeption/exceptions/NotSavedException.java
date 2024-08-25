package com.example.BlogWebSite.exeption.exceptions;

public class NotSavedException extends RuntimeException{
    /**
     * Constructor for NotSavedException.
     *
     * @param message - giving message.
     */
    public NotSavedException(String message) {
        super(message);
    }
}
