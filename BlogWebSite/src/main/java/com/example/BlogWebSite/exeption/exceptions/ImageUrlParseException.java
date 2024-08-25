package com.example.BlogWebSite.exeption.exceptions;

/**
 * Exception that is thrown when parsing of image's URL fails.
 */
public class ImageUrlParseException extends RuntimeException {
    public ImageUrlParseException(String message) {
        super(message);
    }
}
