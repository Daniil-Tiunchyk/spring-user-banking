package com.example.spring_user_banking.exception;

public class DuplicateDataException extends RuntimeException {

    public DuplicateDataException(String message) {
        super(message);
    }

    public DuplicateDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
