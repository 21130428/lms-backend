package com.linh.lms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> emailExceptionHandler(EmailAlreadyExistsException exception) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        return new ResponseEntity<String>(exception.getMessage(), httpStatus);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> usernameExceptionHandler(UsernameAlreadyExistsException exception) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        return new ResponseEntity<String>(exception.getMessage(), httpStatus);
    }
}
