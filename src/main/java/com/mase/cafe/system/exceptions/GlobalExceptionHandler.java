package com.mase.cafe.system.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError; // Import this
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 1. Create a simple list to hold the error messages
        List<String> errorMessages = new ArrayList<>();

        // 2. Loop through the errors one by one
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();

        for (ObjectError error : allErrors) {
            // Get the text (e.g., "Username must be at least 5 characters")
            String message = error.getDefaultMessage();

            // Add it to our list
            errorMessages.add(message);
        }

        // 3. Return the simple list of strings
        return errorMessages;
    }
}
