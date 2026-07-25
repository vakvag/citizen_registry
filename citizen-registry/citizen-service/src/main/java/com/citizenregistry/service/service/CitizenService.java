package com.citizenregistry.service.service;

import com.citizenregistry.domain.Citizen;
import com.citizenregistry.service.dto.CitizenUpdateDto;
import com.citizenregistry.service.exception.CitizenAlreadyExistsException;
import com.citizenregistry.service.exception.CitizenNotFoundException;
import com.citizenregistry.service.exception.InvalidSearchParameterException;
import com.citizenregistry.service.repository.CitizenRepository;
import com.citizenregistry.service.specification.CitizenSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer για τη διαχείριση πολιτών στο μητρώο.
 * Περιέχει την επιχειρηματική λογική και τους ελέγχους εγκυρότητας.
 */
@Service
public class CitizenService {

    private final CitizenRepository citizenRepository;

    public CitizenService(CitizenRepository citizenRepository) {
        this.citizenRepository = citizenRepository;
    }

    /**
     * Εισαγωγή νέου πολίτη στο μητρώο.
     * Ελέγχει αν ο πολίτης υπάρχει ήδη πριν την εισαγωγή.
     *
     * @param citizen ο πολίτης προς εισαγωγή
     * @return ο πολίτης που εισήχθη
     * @throws CitizenAlreadyExistsException αν ο ΑΤ υπάρχει ήδη
     */
    public Citizen createCitizen(Citizen citizen) {
        if (citizenRepository.existsById(citizen.getAt())) {
            throw new CitizenAlreadyExistsException(citizen.getAt());
        }
        return citizenRepository.save(citizen);
    }

    /**
     * Ανάκτηση πολίτη βάσει ΑΤ.
     *
     * @param at ο αριθμός ταυτότητας
     * @return ο πολίτης
     * @throws CitizenNotFoundException αν δεν βρεθεί
     */
    public Citizen getCitizenByAt(String at) {
        return citizenRepository.findById(at)
                .orElseThrow(() -> new CitizenNotFoundException(at));
    }

    /**
     * Ενημέρωση πολίτη - μόνο πεδία ΑΦΜ και Διεύθυνση.
     *
     * @param at        ο αριθμός ταυτότητας του πολίτη
     * @param updateDto τα νέα δεδομένα (ΑΦΜ, Διεύθυνση)
     * @return ο ενημερωμένος πολίτης
     * @throws CitizenNotFoundException αν δεν βρεθεί ο πολίτης
     */
    public Citizen updateCitizen(String at, CitizenUpdateDto updateDto) {
        Citizen citizen = citizenRepository.findById(at)
                .orElseThrow(() -> new CitizenNotFoundException(at));

        if (updateDto.getAfm() != null) {
            citizen.setAfm(updateDto.getAfm());
        }
        if (updateDto.getAddress() != null) {
            citizen.setAddress(updateDto.getAddress());
        }

        return citizenRepository.save(citizen);
    }

    /**
     * Διαγραφή πολίτη βάσει ΑΤ.
     *
     * @param at ο αριθμός ταυτότητας
     * @throws CitizenNotFoundException αν δεν βρεθεί ο πολίτης
     */
    public void deleteCitizen(String at) {
        if (!citizenRepository.existsById(at)) {
            throw new CitizenNotFoundException(at);
        }
        citizenRepository.deleteById(at);
    }

    /**
     * Αναζήτηση πολιτών βάσει οποιουδήποτε πεδίου ή συνδυασμού πεδίων.
     * Ελέγχει την εγκυρότητα των παραμέτρων πριν την αναζήτηση.
     *
     * @return λίστα πολιτών που ταιριάζουν στα κριτήρια
     * @throws InvalidSearchParameterException αν δοθούν μη έγκυρες τιμές
     */
    public List<Citizen> searchCitizens(String at, String firstName, String lastName,
                                        String gender, String dateOfBirth,
                                        String afm, String address) {

        // Επικύρωση παραμέτρων αναζήτησης
        Map<String, String> errors = validateSearchParameters(at, dateOfBirth, afm);
        if (!errors.isEmpty()) {
            throw new InvalidSearchParameterException(errors);
        }

        // Δυναμική κατασκευή query με Specifications
        Specification<Citizen> spec = Specification.where(null);

        if (at != null && !at.isEmpty()) {
            spec = spec.and(CitizenSpecification.hasAt(at));
        }
        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and(CitizenSpecification.hasFirstName(firstName));
        }
        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and(CitizenSpecification.hasLastName(lastName));
        }
        if (gender != null && !gender.isEmpty()) {
            spec = spec.and(CitizenSpecification.hasGender(gender));
        }
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            spec = spec.and(CitizenSpecification.hasDateOfBirth(dateOfBirth));
        }
        if (afm != null && !afm.isEmpty()) {
            spec = spec.and(CitizenSpecification.hasAfm(afm));
        }
        if (address != null && !address.isEmpty()) {
            spec = spec.and(CitizenSpecification.hasAddress(address));
        }

        return citizenRepository.findAll(spec);
    }

    /**
     * Επικύρωση παραμέτρων αναζήτησης (ΑΤ, Ημερομηνία, ΑΦΜ).
     */
    private Map<String, String> validateSearchParameters(String at, String dateOfBirth, String afm) {
        Map<String, String> errors = new HashMap<>();

        if (at != null && !at.isEmpty() && at.length() != 8) {
            errors.put("at", "Ο ΑΤ πρέπει να αποτελείται από ακριβώς 8 χαρακτήρες");
        }
        if (dateOfBirth != null && !dateOfBirth.isEmpty()
                && !dateOfBirth.matches("^\\d{2}-\\d{2}-\\d{4}$")) {
            errors.put("dateOfBirth",
                    "Η Ημερομηνία Γέννησης πρέπει να είναι της μορφής ΧΧ-ΥΥ-ΚΚΚΚ (π.χ. 12-11-2008)");
        }
        if (afm != null && !afm.isEmpty() && !afm.matches("^\\d{9}$")) {
            errors.put("afm", "Το ΑΦΜ πρέπει να αποτελείται από ακριβώς 9 ψηφία");
        }

        return errors;
    }
}
