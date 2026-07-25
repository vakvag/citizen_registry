package com.citizenregistry.service.specification;

import com.citizenregistry.domain.Citizen;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications για δυναμικό φιλτράρισμα πολιτών.
 * Κάθε μέθοδος δημιουργεί ένα Specification που μπορεί να συνδυαστεί
 * με άλλα μέσω and/or για σύνθετες αναζητήσεις.
 */
public class CitizenSpecification {

    private CitizenSpecification() {
        // Utility class - δεν επιτρέπεται instantiation
    }

    /**
     * Φιλτράρισμα βάσει ΑΤ (ακριβής αντιστοίχιση).
     */
    public static Specification<Citizen> hasAt(String at) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("at"), at);
    }

    /**
     * Φιλτράρισμα βάσει ονόματος (μερική αντιστοίχιση, case-insensitive).
     */
    public static Specification<Citizen> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        "%" + firstName.toLowerCase() + "%"
                );
    }

    /**
     * Φιλτράρισμα βάσει επιθέτου (μερική αντιστοίχιση, case-insensitive).
     */
    public static Specification<Citizen> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        "%" + lastName.toLowerCase() + "%"
                );
    }

    /**
     * Φιλτράρισμα βάσει φύλου (ακριβής αντιστοίχιση, case-insensitive).
     */
    public static Specification<Citizen> hasGender(String gender) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("gender")),
                        gender.toLowerCase()
                );
    }

    /**
     * Φιλτράρισμα βάσει ημερομηνίας γέννησης (ακριβής αντιστοίχιση).
     */
    public static Specification<Citizen> hasDateOfBirth(String dateOfBirth) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("dateOfBirth"), dateOfBirth);
    }

    /**
     * Φιλτράρισμα βάσει ΑΦΜ (ακριβής αντιστοίχιση).
     */
    public static Specification<Citizen> hasAfm(String afm) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("afm"), afm);
    }

    /**
     * Φιλτράρισμα βάσει διεύθυνσης (μερική αντιστοίχιση, case-insensitive).
     */
    public static Specification<Citizen> hasAddress(String address) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("address")),
                        "%" + address.toLowerCase() + "%"
                );
    }
}
