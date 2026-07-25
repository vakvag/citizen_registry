package com.citizenregistry.service.exception;

import java.util.Map;

/**
 * Exception που ρίχνεται όταν δοθούν μη έγκυρες παράμετροι αναζήτησης.
 */
public class InvalidSearchParameterException extends RuntimeException {

    private final Map<String, String> errors;

    public InvalidSearchParameterException(Map<String, String> errors) {
        super("Μη έγκυρες παράμετροι αναζήτησης");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
