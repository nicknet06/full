package api.services;

import api.entities.Employee;
import java.util.List;
import java.util.ArrayList;

/**
 * Υπηρεσία που διαχειρίζεται λειτουργίες σχετικές με εργαζομένους,
 * όπως αναζήτηση, έλεγχο στοιχείων, προσθήκη, πιστοποίηση και διαγραφή.
 * @author Μυρτώ Θεοδουλίδου
 * @author Αγγελική Τσευλίκου
 */
public class EmployeeService {

    private final List<Employee> employees = new ArrayList<>();

    /**
     * Εντοπίζει έναν εργαζόμενο με βάση το username του.
     * <p>
     * Πραγματοποιείται έλεγχος εγκυρότητας της εισόδου και στη συνέχεια
     * γίνεται αναζήτηση στη λίστα εργαζομένων, χωρίς διάκριση πεζών/κεφαλαίων.
     * </p>
     *
     * @param username το username προς αναζήτηση
     * @return το αντίστοιχο Employee ή null αν δεν βρεθεί
     * @throws IllegalArgumentException όταν το username είναι κενό ή μη έγκυρο
     */
    public Employee findByUsername(String username){
        if (username == null || username.isBlank()){
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        username = username.trim();

        for (Employee employee : employees){
            if (employee.getUsername().equalsIgnoreCase(username)){
                return employee;
            }
        }
        return null;
    }

    /**
     * Αναζήτηση εργαζομένου βάσει email.
     * <p>
     * Το email ελέγχεται για εγκυρότητα και στη συνέχεια συγκρίνεται
     * με τα αποθηκευμένα στοιχεία, αγνοώντας διαφορές σε πεζά/κεφαλαία.
     * </p>
     *
     * @param email το email προς αναζήτηση
     * @return το Employee αν υπάρχει, αλλιώς null
     * @throws IllegalArgumentException όταν το email είναι κενό ή null
     */
    public Employee findByEmail(String email){
        if (email == null || email.isBlank()){
            throw new IllegalArgumentException("email cannot be empty or null.");
        }

        email = email.trim();

        for (Employee employee : employees){
            if(employee.getEmail().equalsIgnoreCase(email)){
                return employee;
            }
        }
        return null;
    }

    /**
     * Καταχώρηση νέου εργαζομένου στο σύστημα.
     * <p>
     * Πραγματοποιούνται όλοι οι απαραίτητοι έλεγχοι για την εγκυρότητα
     * των πεδίων και τη μοναδικότητα email/username.
     * </p>
     *
     * @param emp ο εργαζόμενος προς προσθήκη
     * @throws IllegalArgumentException αν κάποιο από τα στοιχεία δεν είναι έγκυρο
     */
    public void addEmployee(Employee emp) {

        // Έλεγχος για null αντικείμενο
        if (emp == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        // Έλεγχος email
        if (emp.getEmail() == null || emp.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        String email = emp.getEmail().trim();
        if (findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists!");
        }

        // Έλεγχος username
        if (emp.getUsername() == null || emp.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        String username = emp.getUsername().trim();
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists!");
        }

        // Έλεγχος ονοματεπώνυμου
        if (emp.getFullName() == null || emp.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name cannot be null or empty.");
        }

        // Έλεγχος κωδικού
        if (emp.getPassword() == null || emp.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        // Προσθήκη στη λίστα μετά την επιτυχή επικύρωση
        employees.add(emp);
    }

    /**
     * Έλεγχος στοιχείων σύνδεσης εργαζομένου.
     * <p>
     * Ελέγχει ότι τα στοιχεία έχουν δοθεί σωστά, εντοπίζει τον χρήστη
     * και συγκρίνει τον αποθηκευμένο κωδικό με αυτόν που δόθηκε.
     * </p>
     *
     * @param username το username εισόδου
     * @param password ο κωδικός εισόδου
     * @return true αν τα στοιχεία είναι σωστά, αλλιώς false
     * @throws IllegalArgumentException όταν κάποιο από τα πεδία είναι άδειο
     */
    public boolean validateLogin(String username, String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        username = username.trim();
        password = password.trim();

        // Αναζήτηση χρήστη
        Employee emp = findByUsername(username);
        if (emp == null) {
            return false; // δεν υπάρχει τέτοιος χρήστης
        }

        // Έλεγχος κωδικού
        return emp.getPassword().equals(password);
    }

    /**
     * Αφαιρεί έναν εργαζόμενο από το σύστημα.
     *
     * @param emp ο εργαζόμενος προς διαγραφή
     * @throws IllegalArgumentException αν το αντικείμενο είναι null
     * @throws IllegalStateException αν ο εργαζόμενος δεν υπάρχει στη λίστα
     */
    public void deleteEmployee(Employee emp) {
        if (emp == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }

        // Η remove επιστρέφει true μόνο αν το αντικείμενο υπήρχε
        boolean removed = employees.remove(emp);

        if (!removed) {
            throw new IllegalStateException("Employee not found in list.");
        }
    }

    /**
     * Επιστρέφει όλους τους καταχωρημένους εργαζομένους.
     *
     * @return λίστα με όλα τα Employee αντικείμενα
     */
    public List<Employee> getAllEmployees() {
        return employees; // Επιστρέφεται η ίδια λίστα (με δυνατότητα τροποποίησης)
    }

}
