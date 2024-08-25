package com.example.BlogWebSite.auth.service.impl;

import com.example.BlogWebSite.auth.service.AuthenticationService;
import com.example.BlogWebSite.auth.service.JWTService;
import com.example.BlogWebSite.constant.ErrorMessage;
import com.example.BlogWebSite.exeption.exceptions.UserAlreadyRegisteredException;
import com.example.BlogWebSite.model.Role;
import com.example.BlogWebSite.model.User;
import com.example.BlogWebSite.model.dto.JwtAuthenticationResponse;
import com.example.BlogWebSite.model.dto.RefreshTokenRequest;
import com.example.BlogWebSite.model.dto.SignInRequest;
import com.example.BlogWebSite.model.dto.SignUpRequest;
import com.example.BlogWebSite.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public User signup(SignUpRequest signUpRequest) {
        log.debug("Starting user signup process...");

        User user = new User();
        try {
            user.setUserName(signUpRequest.getUserName());
            user.setEmail(signUpRequest.getEmail());
            user.setRole(Role.USER);
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            log.debug("Saving user to the database...");

            return userRepo.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Error during user signup:", e);
            throw new UserAlreadyRegisteredException(ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_EMAIL);
        }
    }

    public JwtAuthenticationResponse signin(SignInRequest signInRequest) {
        try {
            log.debug("Starting user signin process...");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(),
                    signInRequest.getPassword()));

            log.debug("Fetching user by email...");
            var user = userRepo.findByEmail(signInRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

            log.debug("Generating JWT token...");
            var jwt = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            log.debug("Creating JwtAuthenticationResponse...");
            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);
            return jwtAuthenticationResponse;
        } catch (InternalAuthenticationServiceException e) {
            log.error("Internal authentication service error", e);
            throw new AuthenticationServiceException("Internal authentication service error", e);
        }
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
        User user = userRepo.findByEmail(userEmail).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            var jwt = jwtService.generateToken(user);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthenticationResponse;
        }
        return null;
    }
}

