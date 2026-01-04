package api.entities;

/**
 * Κλάση που περιγράφει έναν πελάτη του συστήματος ενοικίασης.
 * <p>
 * Κάθε πελάτης αναγνωρίζεται από το μοναδικό ΑΦΜ του και
 * συνοδεύεται από βασικά στοιχεία επικοινωνίας όπως ονοματεπώνυμο,
 * τηλέφωνο και email. Η εγκυρότητα των δεδομένων ελέγχεται
 * μέσω των setter μεθόδων.
 * </p>
 *
 * @author Μυρτώ Θεοδουλίδου
 * @author Αγγελική Τσευλίκου
 */
public class Customer {

    //|----------------Μεταβλητές--------------|

    private String afm;         // ΑΦΜ (9 ψηφία)
    private String fullName;    // Ονοματεπώνυμο
    private String phoneNumber; // Τηλέφωνο (10 ψηφία)
    private String email;       // Διεύθυνση email

    //|----------------Constructor--------------|

    /**
     * Δημιουργεί έναν νέο πελάτη με τα απαραίτητα στοιχεία.
     * Οι τιμές περνούν από έλεγχο εγκυρότητας μέσω των setter.
     *
     * @param afm         ΑΦΜ 9 ψηφίων
     * @param fullName    πλήρες όνομα
     * @param phoneNumber τηλέφωνο 10 ψηφίων
     * @param email       email πελάτη
     *
     * @throws IllegalArgumentException αν κάποιο πεδίο δεν είναι έγκυρο
     */
    public Customer(String afm, String fullName, String phoneNumber, String email) {
        setAfm(afm);
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
    }

    //|----------------Getters--------------|

    public String getAfm()         { return afm; }
    public String getFullName()    { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail()       { return email; }

    //|----------------Setters--------------|

    /**
     * Ορίζει το ΑΦΜ του πελάτη.
     * Πρέπει να αποτελείται αποκλειστικά από 9 ψηφία.
     */
    public void setAfm(String afm) {
        if (afm == null || afm.isBlank())
            throw new IllegalArgumentException("AFM cannot be null or empty.");

        afm = afm.trim();

        if (!afm.matches("\\d{9}"))
            throw new IllegalArgumentException("AFM must consist of exactly 9 digits.");

        this.afm = afm;
    }

    /**
     * Ορίζει το ονοματεπώνυμο του πελάτη.
     */
    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Full name cannot be empty.");

        this.fullName = fullName.trim();
    }

    /**
     * Ορίζει τον αριθμό τηλεφώνου.
     * Πρέπει να αποτελείται από ακριβώς 10 ψηφία.
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank())
            throw new IllegalArgumentException("Phone number cannot be empty.");

        phoneNumber = phoneNumber.trim();

        if (!phoneNumber.matches("\\d{10}"))
            throw new IllegalArgumentException("Phone number must have exactly 10 digits.");

        this.phoneNumber = phoneNumber;
    }

    /**
     * Ορίζει το email του πελάτη και ελέγχει τη μορφή του.
     */
    public void setEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be empty.");

        email = email.trim();

        // Απλός έλεγχος μορφής email
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("Invalid email format.");

        this.email = email;
    }

    //|----------------Equals & HashCode--------------|

    /**
     * Δύο πελάτες θεωρούνται ίδιοι όταν έχουν το ίδιο ΑΦΜ.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer c = (Customer) o;
        return afm.equals(c.afm);
    }

    @Override
    public int hashCode() {
        return afm == null ? 0 : afm.hashCode();
    }

    //|----------------toString--------------|

    /**
     * Επιστρέφει μια συνοπτική περιγραφή του πελάτη.
     */
    @Override
    public String toString() {
        return fullName + " (" + afm + ") - " + phoneNumber + " / " + email;
    }

}