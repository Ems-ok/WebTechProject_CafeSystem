package com.mase.cafe.system.exceptions;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException; // Added
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException; // Added

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_KEY = "error";
    private static final String DATE_FORMAT_ERROR = "Invalid date format. Please use YYYY-MM-DD.";
    private static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put(ERROR_KEY, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put(ERROR_KEY, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> handleDateFormatError(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put(ERROR_KEY, DATE_FORMAT_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(ERROR_KEY, "Validation failed");
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransactionException(TransactionSystemException ex) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException) {
            Map<String, String> response = new HashMap<>();
            response.put(ERROR_KEY, "Invalid data: " + cause.getMessage());
            return ResponseEntity.status(400).body(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put(ERROR_KEY, INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(500).body(response);
    }
}