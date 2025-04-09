package com.example.spring_user_banking.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
        log.error("CustomException", e);

        Map<String, Object> resp = new HashMap<>();
        resp.put("error", e.getMessage());
        resp.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("UnauthorizedException", ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "Пользователь не авторизован",
                        "status", HttpStatus.UNAUTHORIZED.value()
                ));
    }

    /**
     * Любые ошибки доступа к данным, не закрытые более конкретными обработчиками.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {
        log.error("DataAccessException", ex);

        Map<String, Object> resp = new HashMap<>();
        resp.put("error", "Произошла ошибка при работе с базой данных");
        resp.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    /**
     * Нарушение целостности (например, попытка вставить дубликат уникального поля).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException", ex);

        Map<String, Object> resp = new HashMap<>();
        resp.put("error", "Нарушение целостности данных");
        resp.put("status", HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
    }

    /**
     * Обработчик SecurityException (попытка сделать то,
     * на что у пользователя нет прав). Возвращает HTTP 403 FORBIDDEN.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException ex) {
        log.warn("SecurityException: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "status", HttpStatus.FORBIDDEN.value()
                ));
    }

    /**
     * Самый общий обработчик на оставшиеся необработанные ошибки.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherExceptions(Exception e) {
        log.error("UnknownException", e);

        Map<String, Object> resp = new HashMap<>();
        resp.put("error", "Внутренняя ошибка сервера");
        resp.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
