package com.citizenregistry.service.dto;

import jakarta.validation.constraints.Pattern;

/**
 * DTO (Data Transfer Object) για την ενημέρωση εγγραφής πολίτη.
 * Επιτρέπει ενημέρωση μόνο των πεδίων ΑΦΜ και Διεύθυνση.
 */
public class CitizenUpdateDto {

    /**
     * ΑΦΜ - Αν δοθεί, πρέπει να αποτελείται από ακριβώς 9 ψηφία.
     */
    @Pattern(regexp = "^\\d{9}$",
            message = "Το ΑΦΜ πρέπει να αποτελείται από ακριβώς 9 ψηφία")
    private String afm;

    /**
     * Διεύθυνση κατοικίας.
     */
    private String address;

    // ==================== Constructors ====================

    public CitizenUpdateDto() {
    }

    public CitizenUpdateDto(String afm, String address) {
        this.afm = afm;
        this.address = address;
    }

    // ==================== Getters & Setters ====================

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
