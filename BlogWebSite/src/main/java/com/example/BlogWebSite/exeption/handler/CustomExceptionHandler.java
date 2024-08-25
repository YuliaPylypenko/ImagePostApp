package com.example.BlogWebSite.exeption.handler;

import com.example.BlogWebSite.exeption.exceptions.BadRequestException;
import com.example.BlogWebSite.exeption.exceptions.NotFoundException;
import com.example.BlogWebSite.exeption.exceptions.NotSavedException;
import com.example.BlogWebSite.exeption.exceptions.UserAlreadyRegisteredException;
import com.example.BlogWebSite.model.dto.ValidationExceptionDto;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private ErrorAttributes errorAttributes;

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<Object> authenticationException(WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }


    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(NotSavedException.class)
    public final ResponseEntity<Object> handleNotSavedException(
            NotSavedException ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        log.trace(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }


    /**
     * Method intercept exception {@link UserAlreadyRegisteredException}.
     *
     * @param ex Exception witch should be intercepted.
     * @return ResponseEntity witch contain http status and body with message of
     * exception.
     */
    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public final ResponseEntity<Object> handleBadEmailException(UserAlreadyRegisteredException ex, WebRequest request) {
        log.info(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(getErrorAttributes(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionResponse);
    }

    private Map<String, Object> getErrorAttributes(WebRequest webRequest) {
        ErrorAttributeOptions options = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.STACK_TRACE,
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.EXCEPTION
        );
        return new HashMap<>(errorAttributes.getErrorAttributes(webRequest, options));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(error -> errors.put(error.getPropertyPath().toString(), error.getMessage()));
        ValidationExceptionDto response = new ValidationExceptionDto("Validation error", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

}
