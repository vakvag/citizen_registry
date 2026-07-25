package com.citizenregistry.service.exception;

/**
 * Exception που ρίχνεται όταν γίνεται προσπάθεια εισαγωγής
 * πολίτη που υπάρχει ήδη στο μητρώο.
 */
public class CitizenAlreadyExistsException extends RuntimeException {

    public CitizenAlreadyExistsException(String at) {
        super("Ο πολίτης με ΑΤ '" + at + "' υπάρχει ήδη στο μητρώο");
    }
}
