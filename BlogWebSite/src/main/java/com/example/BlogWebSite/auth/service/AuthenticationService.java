package com.example.BlogWebSite.auth.service;

import com.example.BlogWebSite.model.User;
import com.example.BlogWebSite.model.dto.JwtAuthenticationResponse;
import com.example.BlogWebSite.model.dto.RefreshTokenRequest;
import com.example.BlogWebSite.model.dto.SignInRequest;
import com.example.BlogWebSite.model.dto.SignUpRequest;

public interface AuthenticationService {
    User signup(SignUpRequest signUpRequest);
    JwtAuthenticationResponse signin(SignInRequest signInRequest);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

}
