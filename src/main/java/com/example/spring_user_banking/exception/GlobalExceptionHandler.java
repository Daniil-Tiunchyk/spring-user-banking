package com.example.spring_user_banking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public Map<String, Object> handleCustomException(CustomException e) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("error", e.getMessage());
        resp.put("status", HttpStatus.BAD_REQUEST.value());
        return resp;
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleOtherExceptions(Exception e) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("error", e.getMessage());
        resp.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return resp;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage(), "status", HttpStatus.UNAUTHORIZED.value()));
    }
}
