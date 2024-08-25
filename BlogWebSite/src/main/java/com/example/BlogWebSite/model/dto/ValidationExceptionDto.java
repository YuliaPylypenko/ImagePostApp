package com.example.BlogWebSite.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
@Data
public class ValidationExceptionDto implements Serializable {
    private String message;
    private Map<String, String> errors;

    public ValidationExceptionDto(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }
}
