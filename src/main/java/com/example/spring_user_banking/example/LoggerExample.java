package com.example.spring_user_banking.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerExample {
    private static final Logger logger = LoggerFactory.getLogger(LoggerExample.class);

    public static void main(String[] args) {
        logger.info("Application started.");
        logger.debug("This is a debug message."); // Будет отображаться, если уровень логирования настроен на debug
        logger.error("An error occurred.", new RuntimeException("Test exception"));
    }
}
