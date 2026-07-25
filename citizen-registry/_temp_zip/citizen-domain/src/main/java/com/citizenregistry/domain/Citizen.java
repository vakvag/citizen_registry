package com.citizenregistry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entity class που αναπαριστά έναν Πολίτη στο Μητρώο Πολιτών.
 */
@Entity
@Table(name = "citizens")
public class Citizen {

    /**
     * Αριθμός Ταυτότητας (ΑΤ) - Primary Key.
     * Υποχρεωτικό πεδίο, ακριβώς 8 χαρακτήρες.
     */
    @Id
    @NotBlank(message = "Ο ΑΤ είναι υποχρεωτικός")
    @Size(min = 8, max = 8, message = "Ο ΑΤ πρέπει να αποτελείται από ακριβώς 8 χαρακτήρες")
    @Column(name = "at_number", length = 8, nullable = false, unique = true)
    private String at;

    /**
     * Όνομα πολίτη - Υποχρεωτικό πεδίο.
     */
    @NotBlank(message = "Το Όνομα είναι υποχρεωτικό")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Επίθετο πολίτη - Υποχρεωτικό πεδίο.
     */
    @NotBlank(message = "Το Επίθετο είναι υποχρεωτικό")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Φύλο πολίτη - Υποχρεωτικό πεδίο.
     */
    @NotBlank(message = "Το Φύλο είναι υποχρεωτικό")
    @Column(name = "gender", nullable = false)
    private String gender;

    /**
     * Ημερομηνία γέννησης - Υποχρεωτικό πεδίο.
     * Μορφή: ΧΧ-ΥΥ-ΚΚΚΚ (π.χ. 12-11-2008)
     */
    @NotBlank(message = "Η Ημερομηνία Γέννησης είναι υποχρεωτική")
    @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{4}$",
            message = "Η Ημερομηνία Γέννησης πρέπει να είναι της μορφής ΧΧ-ΥΥ-ΚΚΚΚ (π.χ. 12-11-2008)")
    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;

    /**
     * ΑΦΜ - Μη υποχρεωτικό πεδίο.
     * Αν δοθεί, πρέπει να αποτελείται από ακριβώς 9 ψηφία.
     */
    @Pattern(regexp = "^\\d{9}$",
            message = "Το ΑΦΜ πρέπει να αποτελείται από ακριβώς 9 ψηφία")
    @Column(name = "afm", length = 9)
    private String afm;

    /**
     * Διεύθυνση κατοικίας - Μη υποχρεωτικό πεδίο.
     */
    @Column(name = "address")
    private String address;

    // ==================== Constructors ====================

    /**
     * Default constructor (απαιτείται από JPA).
     */
    public Citizen() {
    }

    /**
     * Constructor με όλα τα πεδία.
     */
    public Citizen(String at, String firstName, String lastName, String gender,
                   String dateOfBirth, String afm, String address) {
        this.at = at;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.afm = afm;
        this.address = address;
    }

    // ==================== Getters & Setters ====================

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

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

    // ==================== toString ====================

    @Override
    public String toString() {
        return "Citizen{" +
                "at='" + at + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", afm='" + afm + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
