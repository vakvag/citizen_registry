package com.citizenregistry.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Κεντρική κλάση εκκίνησης της εφαρμογής Spring Boot
 * για το Μητρώο Πολιτών.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.citizenregistry.domain")
public class CitizenRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CitizenRegistryApplication.class, args);
    }
}
