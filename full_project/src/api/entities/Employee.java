package api.entities;

/**
 * Αντιπροσωπεύει έναν εργαζόμενο της εταιρείας ενοικιάσεων.
 * <p>
 * Οι εργαζόμενοι είναι οι μόνοι που έχουν πρόσβαση στο σύστημα,
 * και κάθε ένας διαθέτει μοναδικό όνομα χρήστη και email,
 * καθώς και βασικά στοιχεία όπως ονοματεπώνυμο και κωδικό.
 * Η κλάση περιλαμβάνει ελέγχους εγκυρότητας μέσω των setter
 * και παρέχει μέθοδο για έλεγχο στοιχείων σύνδεσης.
 * </p>
 *
 * @author Μυρτώ Θεοδουλίδου
 * @author Αγγελική Τσευλίκου
 */
public class Employee {

    //|----------------Πεδία--------------|

    private String fullName;   // Ονοματεπώνυμο εργαζομένου
    private String username;   // Μοναδικό username (χρησιμοποιείται για login)
    private String email;      // Μοναδική διεύθυνση email
    private String password;   // Κωδικός πρόσβασης (αποθηκεύεται ως plain text για την εργασία)

    //|----------------Constructor--------------|

    /**
     * Δημιουργεί έναν νέο εργαζόμενο με όλα τα απαραίτητα στοιχεία.
     *
     * @param fullName ονοματεπώνυμο
     * @param username μοναδικό username
     * @param email    μοναδικό email
     * @param password κωδικός πρόσβασης
     */
    public Employee(String fullName, String username, String email, String password) {
        this.setFullName(fullName);
        this.setUsername(username);
        this.setEmail(email);
        this.setPassword(password);
    }

    //|----------------Getters--------------|

    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    //|----------------Setters με Ελέγχους--------------|

    /**
     * Ορίζει το ονοματεπώνυμο του εργαζομένου.
     *
     * @param fullName το όνομα προς καταχώρηση
     * @throws IllegalArgumentException αν είναι κενό ή null
     */
    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Full name cannot be empty.");
        this.fullName = fullName.trim();
    }

    /**
     * Ορίζει το username του εργαζομένου.
     *
     * @param username το username προς καταχώρηση
     * @throws IllegalArgumentException αν είναι κενό ή null
     */
    public void setUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        this.username = username.trim();
    }

    /**
     * Ορίζει το email του εργαζομένου και ελέγχει τη μορφή του.
     *
     * @param email το email προς καταχώρηση
     * @throws IllegalArgumentException αν είναι κενό ή δεν έχει σωστή μορφή
     */
    public void setEmail(String email) {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email cannot be empty.");

        email = email.trim();

        // Απλός regex έλεγχος για βασική εγκυρότητα email
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("Invalid email format.");

        this.email = email;
    }

    /**
     * Ορίζει τον κωδικό πρόσβασης.
     * Δεν γίνεται trim, καθώς μπορεί να περιέχει κενά.
     *
     * @param password ο κωδικός προς καταχώρηση
     * @throws IllegalArgumentException αν είναι κενός ή null
     */
    public void setPassword(String password) {
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password cannot be empty.");
        this.password = password;
    }

    //|----------------Equals & HashCode--------------|

    /**
     * Δύο εργαζόμενοι θεωρούνται ίδιοι όταν έχουν το ίδιο username,
     * χωρίς διάκριση πεζών/κεφαλαίων.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee e = (Employee) o;
        return username.equalsIgnoreCase(e.username);
    }

    /**
     * Το hashCode βασίζεται στο username σε πεζή μορφή,
     * ώστε να είναι συνεπές με το equals().
     */
    @Override
    public int hashCode() {
        return username == null ? 0 : username.toLowerCase().hashCode();
    }

    //|----------------Login Match--------------|

    /**
     * Ελέγχει αν τα στοιχεία σύνδεσης ταιριάζουν με αυτά του εργαζομένου.
     *
     * @param username το username προς έλεγχο (case-insensitive)
     * @param password ο κωδικός προς έλεγχο (case-sensitive)
     * @return true αν ταιριάζουν, αλλιώς false
     */
    public boolean loginMatch(String username, String password) {
        return this.username.equalsIgnoreCase(username) &&
                this.password.equals(password);
    }

    //|----------------toString--------------|

    /**
     * Επιστρέφει μια απλή αναπαράσταση του εργαζομένου.
     */
    @Override
    public String toString() {
        return fullName + " (" + username + ") - " + email;
    }

}
