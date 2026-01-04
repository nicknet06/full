package api.services;

import api.entities.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * Υπηρεσία που αναλαμβάνει τη διαχείριση πελατών:
 * δημιουργία, αναζήτηση, ενημέρωση, διαγραφή και
 * γενικό χειρισμό των σχετικών εγγραφών.
 *
 * Οι λειτουργίες περιλαμβάνουν βασικούς ελέγχους εγκυρότητας
 * και αναζήτηση με πολλαπλά κριτήρια.
 *
 * @author Μυρτώ Θεοδουλίδου
 * @author Αγγελική Τσευλίκου
 */
public class CustomerService {

    private final List<Customer> customers = new ArrayList<>();

    /**
     * Αναζητά πελάτη με βάση το ΑΦΜ.
     *
     * @param afm το ΑΦΜ προς αναζήτηση
     * @return τον αντίστοιχο πελάτη ή null αν δεν βρεθεί
     */
    public Customer findByAfm(String afm) {
        if (afm == null || afm.isBlank()) {
            return null;
        }

        afm = afm.trim();

        for (Customer c : customers) {
            if (c.getAfm() != null && c.getAfm().equals(afm)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Προσθέτει νέο πελάτη στο σύστημα.
     * Πραγματοποιούνται έλεγχοι εγκυρότητας:
     * - το αντικείμενο δεν πρέπει να είναι null
     * - το ΑΦΜ πρέπει να υπάρχει και να είναι μοναδικό
     * - ονοματεπώνυμο, τηλέφωνο και email πρέπει να είναι συμπληρωμένα
     *
     * @param customer ο πελάτης προς καταχώρηση
     */
    public void addCustomer(Customer customer) {

        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }

        // Έλεγχος ΑΦΜ
        if (customer.getAfm() == null || customer.getAfm().isBlank()) {
            throw new IllegalArgumentException("AFM cannot be null or empty.");
        }

        String afm = customer.getAfm().trim();

        if (findByAfm(afm) != null) {
            throw new IllegalArgumentException("A customer with this AFM already exists.");
        }

        // Έλεγχος ονοματεπώνυμου
        if (customer.getFullName() == null || customer.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }

        // Έλεγχος τηλεφώνου
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone cannot be empty.");
        }

        // Έλεγχος email
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        customers.add(customer);
    }

    /**
     * Ενημερώνει τα στοιχεία ενός υπάρχοντος πελάτη.
     *
     * @param afm     το ΑΦΜ του πελάτη που θα ενημερωθεί
     * @param newData τα νέα δεδομένα
     */
    public void updateCustomer(String afm, Customer newData) {

        if (afm == null || afm.isBlank()) {
            throw new IllegalArgumentException("AFM cannot be empty.");
        }

        if (newData == null) {
            throw new IllegalArgumentException("New data cannot be null.");
        }

        afm = afm.trim();

        Customer existing = findByAfm(afm);
        if (existing == null) {
            throw new IllegalArgumentException("Customer with AFM " + afm + " does not exist.");
        }

        // Το ΑΦΜ δεν αλλάζει ποτέ
        if (newData.getAfm() == null || !afm.equals(newData.getAfm().trim())) {
            throw new IllegalArgumentException("AFM cannot be changed.");
        }

        // Έλεγχος νέων στοιχείων
        if (newData.getFullName() == null || newData.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }

        if (newData.getPhoneNumber() == null || newData.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        if (newData.getEmail() == null || newData.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        // Ενημέρωση πεδίου
        existing.setFullName(newData.getFullName());
        existing.setPhoneNumber(newData.getPhoneNumber());
        existing.setEmail(newData.getEmail());
    }

    /**
     * Διαγράφει πελάτη από το σύστημα.
     *
     * @param afm το ΑΦΜ του πελάτη προς διαγραφή
     */
    public void deleteCustomer(String afm) {

        if (afm == null || afm.isBlank()) {
            throw new IllegalArgumentException("AFM cannot be empty.");
        }

        afm = afm.trim();

        Customer existing = findByAfm(afm);

        if (existing == null) {
            throw new IllegalArgumentException("Customer with AFM " + afm + " does not exist.");
        }

        customers.remove(existing);
    }

    /**
     * Αναζήτηση πελατών με προαιρετικά κριτήρια:
     * ΑΦΜ, ονοματεπώνυμο ή αριθμό τηλεφώνου.
     * Κενές τιμές αγνοούνται.
     *
     * @return λίστα με τους πελάτες που ταιριάζουν στα κριτήρια
     */
    public List<Customer> searchCustomers(String afm, String fullName, String phoneNumber) {

        List<Customer> results = new ArrayList<>();

        if (afm != null) afm = afm.trim();
        if (fullName != null) fullName = fullName.trim();
        if (phoneNumber != null) phoneNumber = phoneNumber.trim();

        for (Customer c : customers) {

            if (afm != null && !afm.isBlank()) {
                if (!c.getAfm().equalsIgnoreCase(afm)) continue;
            }

            if (fullName != null && !fullName.isBlank()) {
                if (!c.getFullName().equalsIgnoreCase(fullName)) continue;
            }

            if (phoneNumber != null && !phoneNumber.isBlank()) {
                if (!c.getPhoneNumber().equals(phoneNumber)) continue;
            }

            results.add(c);
        }

        return results;
    }

    /**
     * Επιστρέφει όλους τους πελάτες του συστήματος.
     */
    public List<Customer> getAllCustomers() {
        return customers;
    }

}
