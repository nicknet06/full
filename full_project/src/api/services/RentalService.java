package api.services;

import api.entities.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalService {

    private final List<Rental> rentals = new ArrayList<>();

    /**
     * Δημιουργεί το επόμενο διαθέσιμο rental ID.
     * Νέο format: RENT-XXXX (π.χ. RENT-0001, RENT-0002).
     */
    public String generateRentalId() {

        int highestNumber = 0;

        for (Rental rental : rentals) {

            String id = rental.getRentalId();

            // Ελέγχουμε ότι το ID έχει το σωστό prefix
            if (id != null && id.startsWith("RENT-")) {

                String numericPart = id.substring(5); // μετά το "RENT-"

                try {
                    int value = Integer.parseInt(numericPart);
                    if (value > highestNumber) {
                        highestNumber = value;
                    }
                } catch (NumberFormatException ignored) {
                    // αγνοούμε IDs που δεν έχουν έγκυρο αριθμό
                }
            }
        }

        int next = highestNumber + 1;
        return String.format("RENT-%04d", next);
    }

    /**
     * Αναζητά μια ενοικίαση με βάση το μοναδικό της ID.
     * Επιστρέφει το αντίστοιχο αντικείμενο Rental ή null
     * όταν δεν εντοπιστεί κάποια αντιστοιχία.
     */
    public Rental findById(String rentalId) {

        if (rentalId == null || rentalId.isBlank())
            return null;

        rentalId = rentalId.trim();

        for (Rental rental : rentals) {
            if (rental.getRentalId().equalsIgnoreCase(rentalId)) {
                return rental;
            }
        }

        return null;
    }

    /**
     * Προσθέτει μια νέα ενοικίαση στο σύστημα.
     * Πριν γίνει η καταχώρηση, πραγματοποιούνται οι εξής έλεγχοι:
     *  - το αντικείμενο rental πρέπει να είναι έγκυρο
     *  - το rental ID δεν πρέπει να υπάρχει ήδη
     *  - το όχημα πρέπει να είναι διαθέσιμο (status = true)
     *  - οι ημερομηνίες πρέπει να είναι σωστές και να μην συγκρούονται
     *    με άλλες ενεργές ενοικιάσεις του ίδιου οχήματος
     *
     * @return true αν η καταχώρηση ολοκληρωθεί, αλλιώς false
     */
    public boolean addRental(Rental rental) {

        if (rental == null) return false;

        // Έλεγχος μοναδικότητας rentalId
        for (Rental r : rentals) {
            if (r.getRentalId().equalsIgnoreCase(rental.getRentalId())) {
                return false;
            }
        }

        // Το όχημα πρέπει να είναι διαθέσιμο (true = available)
        if (!rental.getCar().getStatus()) {
            return false;
        }

        // Έλεγχος ημερομηνιών
        LocalDate startDate = rental.getStartDate();
        LocalDate endDate   = rental.getEndDate();

        if (startDate == null || endDate == null) return false;
        if (endDate.isBefore(startDate)) return false;

        // Έλεγχος για επικαλύψεις με άλλες ενεργές ενοικιάσεις του ίδιου οχήματος
        for (Rental r : rentals) {

            boolean sameCar = r.getCar().equals(rental.getCar());
            boolean active  = r.isActive(); // τώρα είναι boolean
            boolean overlaps =
                    !endDate.isBefore(r.getStartDate()) &&
                            !startDate.isAfter(r.getEndDate());

            if (sameCar && active && overlaps) {
                return false;
            }
        }

        // Καταχώρηση ενοικίασης
        rentals.add(rental);

        // Το όχημα πλέον θεωρείται rented → status = false
        rental.getCar().setStatus(false);

        return true;
    }

    /**
     * Δημιουργεί μια νέα ενοικίαση χρησιμοποιώντας απλά δεδομένα
     * αντί για έτοιμο αντικείμενο Rental.
     *
     * Διαδικασία:
     *  - έλεγχος εγκυρότητας εισόδου
     *  - επιβεβαίωση ότι το όχημα είναι διαθέσιμο
     *  - έλεγχος για πιθανές επικαλύψεις ημερομηνιών
     *  - παραγωγή νέου rental ID
     *  - δημιουργία και καταχώρηση της ενοικίασης
     *
     * @return true αν η ενοικίαση δημιουργηθεί επιτυχώς
     */
    public boolean rentCar(Car car, Customer customer, Employee employee,
                           LocalDate startDate, LocalDate endDate) {

        if (car == null || customer == null || employee == null) return false;
        if (startDate == null || endDate == null) return false;
        if (endDate.isBefore(startDate)) return false;

        // Το όχημα πρέπει να είναι διαθέσιμο (true = available)
        if (!car.getStatus()) return false;

        // Έλεγχος για επικαλύψεις με ενεργές ενοικιάσεις του ίδιου οχήματος
        for (Rental r : rentals) {
            boolean sameCar = r.getCar().equals(car);
            boolean active  = r.isActive(); // boolean πλέον
            boolean overlaps =
                    !endDate.isBefore(r.getStartDate()) &&
                            !startDate.isAfter(r.getEndDate());

            if (sameCar && active && overlaps) return false;
        }

        String rentalId = generateRentalId();

        Rental rental = new Rental(
                rentalId, car, customer, employee,
                startDate, endDate
        );

        rentals.add(rental);

        // Το όχημα πλέον θεωρείται rented → status = false
        car.setStatus(false);

        return true;
    }

    /**
     * Ολοκληρώνει μια ενοικίαση με βάση το ID της.
     * Αν η ενοικίαση εντοπιστεί και βρίσκεται ακόμη σε ενεργή κατάσταση,
     * τότε θεωρείται ολοκληρωμένη και το όχημα γίνεται ξανά διαθέσιμο.
     *
     * @return true αν η διαδικασία ολοκληρωθεί επιτυχώς,
     *         false αν δεν βρεθεί η ενοικίαση ή έχει ήδη ολοκληρωθεί.
     */
    public boolean returnCar(String rentalId) {

        if (rentalId == null || rentalId.isBlank()) return false;

        for (Rental rental : rentals) {

            if (rental.getRentalId().equalsIgnoreCase(rentalId)) {

                // Αν η ενοικίαση έχει ήδη ολοκληρωθεί → δεν κάνουμε τίποτα
                if (!rental.isActive()) {
                    return false;
                }

                // Ολοκλήρωση ενοικίασης (θέτει status = false και κάνει το αυτοκίνητο διαθέσιμο)
                rental.completeRental();
                return true;
            }
        }

        return false;
    }

    /**
     * Επιστρέφει όλες τις ενοικιάσεις που αντιστοιχούν
     * σε συγκεκριμένο ΑΦΜ πελάτη.
     * Αν το ΑΦΜ είναι άκυρο ή δεν υπάρχουν σχετικές εγγραφές,
     * επιστρέφεται κενή λίστα.
     */
    public List<Rental> getRentalsByCustomer(String afm) {

        if (afm == null || afm.isBlank()) {
            return List.of();
        }

        List<Rental> results = new ArrayList<>();

        for (Rental rental : rentals) {
            if (rental.getCustomer().getAfm().equalsIgnoreCase(afm)) {
                results.add(rental);
            }
        }

        return results;
    }

    /**
     * Επιστρέφει όλες τις ενοικιάσεις που σχετίζονται
     * με ένα συγκεκριμένο όχημα, αναζητώντας το μέσω της πινακίδας του.
     * Αν η πινακίδα είναι άκυρη ή δεν υπάρχουν αντίστοιχες εγγραφές,
     * επιστρέφεται κενή λίστα.
     */
    public List<Rental> getRentalsByCar(String plate) {

        if (plate == null || plate.isBlank()) {
            return List.of();
        }

        List<Rental> results = new ArrayList<>();

        for (Rental rental : rentals) {
            if (rental.getCar().getPlate().equalsIgnoreCase(plate)) {
                results.add(rental);
            }
        }

        return results;
    }

    /**
     * Επιστρέφει όλες τις ενοικιάσεις που βρίσκονται ακόμη σε ενεργή κατάσταση.
     * Μια ενοικίαση θεωρείται ενεργή όταν το status της είναι true,
     * δηλαδή όταν το όχημα δεν έχει επιστραφεί ακόμη.
     *
     * @return λίστα με όλες τις ενεργές ενοικιάσεις.
     *         Αν δεν υπάρχουν ενεργές εγγραφές, επιστρέφεται κενή λίστα.
     */
    public List<Rental> getActiveRentals() {

        List<Rental> results = new ArrayList<>();

        for (Rental r : rentals) {
            if (r.isActive()) {   // boolean status: true = active
                results.add(r);
            }
        }

        return results;
    }

    /**
     * Ελέγχει αν υπάρχει ήδη ενοικίαση με το συγκεκριμένο ID.
     * Επιστρέφει true όταν εντοπιστεί αντιστοιχία, αλλιώς false.
     */
    public boolean rentalIdExists(String id) {
        if (id == null || id.isBlank()) return false;

        for (Rental r : rentals) {
            if (r.getRentalId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Παρέχει τη συνολική λίστα όλων των ενοικιάσεων
     * που έχουν καταχωρηθεί στο σύστημα.
     */
    public List<Rental> getAllRentals() {
        return rentals;
    }




}
