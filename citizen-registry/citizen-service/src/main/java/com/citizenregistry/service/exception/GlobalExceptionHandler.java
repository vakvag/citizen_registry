package com.citizenregistry.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Κεντρικός χειριστής εξαιρέσεων (Global Exception Handler).
 * Μετατρέπει τις εξαιρέσεις σε κατάλληλες HTTP απαντήσεις JSON.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Χειρισμός σφαλμάτων επικύρωσης (Bean Validation).
     * Επιστρέφει 400 Bad Request με λεπτομερή μηνύματα ανά πεδίο.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Σφάλμα επικύρωσης δεδομένων");

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Χειρισμός μη εύρεσης πολίτη.
     * Επιστρέφει 404 Not Found.
     */
    @ExceptionHandler(CitizenNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCitizenNotFound(
            CitizenNotFoundException ex) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Χειρισμός διπλότυπης εγγραφής.
     * Επιστρέφει 409 Conflict.
     */
    @ExceptionHandler(CitizenAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCitizenAlreadyExists(
            CitizenAlreadyExistsException ex) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CONFLICT.value());
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Χειρισμός μη έγκυρων παραμέτρων αναζήτησης.
     * Επιστρέφει 400 Bad Request με λεπτομερή μηνύματα ανά πεδίο.
     */
    @ExceptionHandler(InvalidSearchParameterException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSearchParameter(
            InvalidSearchParameterException ex) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());
        response.put("errors", ex.getErrors());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
