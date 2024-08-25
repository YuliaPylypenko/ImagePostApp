package com.example.BlogWebSite.model.dto;

import com.example.BlogWebSite.constant.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class SignUpRequest {
    @Pattern(
            regexp = ValidationConstants.USERNAME_REGEXP,
            message = ValidationConstants.USERNAME_MESSAGE)
    private String userName;
    @Email(message = ValidationConstants.INVALID_EMAIL)
    @NotBlank
    private String email;
    @Pattern(regexp = ValidationConstants.PASSWORD_REGEXP)
    private String password;
}
