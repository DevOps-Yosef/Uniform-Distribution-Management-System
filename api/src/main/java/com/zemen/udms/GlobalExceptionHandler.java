package com.zemen.udms;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        // System.out.println(e.getMessage());
        return new ResponseEntity<>("An error occurred. ", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
