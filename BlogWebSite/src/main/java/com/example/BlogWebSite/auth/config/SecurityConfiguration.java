package com.example.BlogWebSite.auth.config;

import com.example.BlogWebSite.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling((exception) -> exception.authenticationEntryPoint((req, resp, exc) -> {
//                            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            resp.setContentType("application/json");
//                            resp.getWriter().write("{\"message\": \"Authorize first.\"}");
//                        })
//                        .accessDeniedHandler((req, resp, exc) -> {
//                            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                            resp.setContentType("application/json");
//                            resp.getWriter().write("{\"message\": \"You don't have authorities.\"}");
//                        }))
                .authorizeHttpRequests(request -> request.requestMatchers(AUTH_WHITELIST)
                        .permitAll()
                        .anyRequest().authenticated());

        log.info("Security configuration applied.");

        return http.build();
    }
    private static final String[] AUTH_WHITELIST =
            {
                    "/api/v1/auth/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
            };

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        log.info("Authentication provider created.");
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        log.info("Authentication manager created.");
        return config.getAuthenticationManager();
    }
}

