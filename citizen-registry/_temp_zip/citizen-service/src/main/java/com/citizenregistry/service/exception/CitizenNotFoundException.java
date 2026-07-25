package com.citizenregistry.service.exception;

/**
 * Exception που ρίχνεται όταν δεν βρεθεί πολίτης με τον δοθέντα ΑΤ.
 */
public class CitizenNotFoundException extends RuntimeException {

    public CitizenNotFoundException(String at) {
        super("Ο πολίτης με ΑΤ '" + at + "' δεν βρέθηκε στο μητρώο");
    }
}
