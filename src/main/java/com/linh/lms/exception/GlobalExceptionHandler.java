package com.linh.lms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> emailExceptionHandler(EmailAlreadyExistsException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> usernameExceptionHandler(UsernameAlreadyExistsException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> validExceptionHandler(MethodArgumentNotValidException exception) {
        Map<String, List<String>> errors = new HashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            if (errors.containsKey(error.getField())) {
                errors.get(error.getField()).add(error.getDefaultMessage());
            } else {
                List<String> messages = new ArrayList<>();
                messages.add(error.getDefaultMessage());
                errors.put(error.getField(), messages);
            }
        }
        return new ResponseEntity<Map<String, List<String>>>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> invalidCredentialsExceptionHandler(InvalidCredentialsException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<String> accountLockedExceptionHandler(AccountLockedException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<String> passwordMismatchExceptionHandler(PasswordMismatchException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<String> invalidRefreshTokenExceptionHandler(InvalidRefreshTokenException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
