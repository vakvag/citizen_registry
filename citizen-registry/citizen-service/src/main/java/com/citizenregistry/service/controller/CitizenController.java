package com.citizenregistry.service.controller;

import com.citizenregistry.domain.Citizen;
import com.citizenregistry.service.dto.CitizenUpdateDto;
import com.citizenregistry.service.service.CitizenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller για τη διαχείριση του Μητρώου Πολιτών.
 * Παρέχει endpoints για Εισαγωγή, Εμφάνιση, Ενημέρωση, Διαγραφή και Αναζήτηση.
 */
@RestController
@RequestMapping("/api/citizens")
public class CitizenController {

    private final CitizenService citizenService;

    public CitizenController(CitizenService citizenService) {
        this.citizenService = citizenService;
    }

    /**
     * Εισαγωγή νέου πολίτη στο μητρώο.
     * POST /api/citizens
     *
     * @param citizen τα στοιχεία του πολίτη (JSON)
     * @return 201 Created με τον πολίτη, ή 400/409 σε σφάλμα
     */
    @PostMapping
    public ResponseEntity<Citizen> createCitizen(@Valid @RequestBody Citizen citizen) {
        Citizen created = citizenService.createCitizen(citizen);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Εμφάνιση στοιχείων πολίτη βάσει ΑΤ.
     * GET /api/citizens/{at}
     *
     * @param at ο αριθμός ταυτότητας
     * @return 200 OK με τον πολίτη, ή 400/404 σε σφάλμα
     */
    @GetMapping("/{at}")
    public ResponseEntity<?> getCitizen(@PathVariable String at) {
        // Επικύρωση μορφής ΑΤ
        if (at == null || at.length() != 8) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("message",
                    "Μη έγκυρος ΑΤ. Ο ΑΤ πρέπει να αποτελείται από ακριβώς 8 χαρακτήρες");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Citizen citizen = citizenService.getCitizenByAt(at);
        return ResponseEntity.ok(citizen);
    }

    /**
     * Ενημέρωση εγγραφής πολίτη (μόνο ΑΦΜ και Διεύθυνση).
     * PUT /api/citizens/{at}
     *
     * @param at        ο αριθμός ταυτότητας
     * @param updateDto τα νέα δεδομένα
     * @return 200 OK με τον ενημερωμένο πολίτη, ή 400/404 σε σφάλμα
     */
    @PutMapping("/{at}")
    public ResponseEntity<?> updateCitizen(@PathVariable String at,
                                           @Valid @RequestBody CitizenUpdateDto updateDto) {
        // Επικύρωση μορφής ΑΤ
        if (at == null || at.length() != 8) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("message",
                    "Μη έγκυρος ΑΤ. Ο ΑΤ πρέπει να αποτελείται από ακριβώς 8 χαρακτήρες");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Citizen updated = citizenService.updateCitizen(at, updateDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Διαγραφή πολίτη βάσει ΑΤ.
     * DELETE /api/citizens/{at}
     *
     * @param at ο αριθμός ταυτότητας
     * @return 200 OK με μήνυμα επιτυχίας, ή 400/404 σε σφάλμα
     */
    @DeleteMapping("/{at}")
    public ResponseEntity<?> deleteCitizen(@PathVariable String at) {
        // Έλεγχος κενού ΑΤ
        if (at == null || at.trim().isEmpty()) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("message", "Ο ΑΤ δεν μπορεί να είναι κενός");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        // Επικύρωση μορφής ΑΤ
        if (at.length() != 8) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("message",
                    "Μη έγκυρος ΑΤ. Ο ΑΤ πρέπει να αποτελείται από ακριβώς 8 χαρακτήρες");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        citizenService.deleteCitizen(at);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Ο πολίτης με ΑΤ '" + at + "' διαγράφηκε επιτυχώς");
        return ResponseEntity.ok(response);
    }

    /**
     * Αναζήτηση πολιτών βάσει οποιουδήποτε πεδίου ή συνδυασμού πεδίων.
     * GET /api/citizens/search?firstName=...&lastName=...&gender=...
     *
     * @return 200 OK με λίστα πολιτών, ή 400 σε μη έγκυρες παραμέτρους
     */
    @GetMapping("/search")
    public ResponseEntity<List<Citizen>> searchCitizens(
            @RequestParam(required = false) String at,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String afm,
            @RequestParam(required = false) String address) {

        List<Citizen> results = citizenService.searchCitizens(
                at, firstName, lastName, gender, dateOfBirth, afm, address);
        return ResponseEntity.ok(results);
    }
}
