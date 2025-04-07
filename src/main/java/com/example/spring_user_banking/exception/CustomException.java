package com.example.spring_user_banking.exception;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
