package api.entities;

import java.time.LocalDate;

/**
 * Αντικείμενο που περιγράφει μια ενοικίαση οχήματος από πελάτη.
 * <p>
 * Κάθε ενοικίαση διαθέτει μοναδικό κωδικό, συνδέεται με ένα συγκεκριμένο
 * αυτοκίνητο και έναν πελάτη, καταγράφει τον υπάλληλο που την δημιούργησε
 * και περιλαμβάνει ημερομηνίες έναρξης και λήξης. Οι νέες ενοικιάσεις
 * θεωρούνται ενεργές μέχρι να ολοκληρωθούν.
 * </p>
 *
 * @author Μυρτώ Θεοδουλίδου
 * @author Αγγελική Τσευλίκου
 */
public class Rental {

    //|----------------Πεδία--------------|

    private String rentalId;      // Μοναδικός κωδικός ενοικίασης
    private Car car;              // Το όχημα που ενοικιάζεται
    private Customer customer;    // Ο πελάτης που το ενοικιάζει
    private Employee employee;    // Ο υπάλληλος που καταχώρησε την ενοικίαση
    private LocalDate startDate;  // Ημερομηνία έναρξης
    private LocalDate endDate;    // Ημερομηνία λήξης
    private boolean status;       // true = ενεργή, false = ολοκληρωμένη

    //|----------------Constructor--------------|

    /**
     * Δημιουργεί μια νέα ενοικίαση με όλα τα απαραίτητα στοιχεία.
     *
     * @param rentalId   μοναδικό αναγνωριστικό
     * @param car        το όχημα που ενοικιάζεται
     * @param customer   ο πελάτης που το παραλαμβάνει
     * @param employee   ο υπάλληλος που χειρίζεται τη διαδικασία
     * @param startDate  ημερομηνία έναρξης
     * @param endDate    ημερομηνία λήξης
     *
     * @throws IllegalArgumentException αν κάποιο πεδίο είναι άκυρο
     */
    public Rental(String rentalId, Car car, Customer customer, Employee employee,
                  LocalDate startDate, LocalDate endDate) {

        // Έλεγχος υποχρεωτικών πεδίων
        if (rentalId == null || rentalId.isBlank())
            throw new IllegalArgumentException("Rental ID cannot be null or empty.");

        if (car == null)
            throw new IllegalArgumentException("Car cannot be null.");

        if (customer == null)
            throw new IllegalArgumentException("Customer cannot be null.");

        if (employee == null)
            throw new IllegalArgumentException("Employee cannot be null.");

        if (startDate == null)
            throw new IllegalArgumentException("Start date cannot be null.");

        if (endDate == null)
            throw new IllegalArgumentException("End date cannot be null.");

        // Έλεγχος σωστής χρονικής σειράς
        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException("End date cannot be before start date.");

        // Ανάθεση τιμών
        this.rentalId = rentalId.trim();
        this.car = car;
        this.customer = customer;
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;

        // Κάθε νέα ενοικίαση ξεκινά ως ενεργή
        this.status = true;
    }

    //|----------------Getters--------------|

    public String getRentalId()   { return rentalId; }
    public Car getCar()           { return car; }
    public Customer getCustomer() { return customer; }
    public Employee getEmployee() { return employee; }
    public LocalDate getStartDate(){ return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActive()     { return status; }

    //|----------------Setters--------------|

    /**
     * Ενημερώνει την ημερομηνία λήξης της ενοικίασης.
     * Η νέα ημερομηνία πρέπει να είναι μεταγενέστερη ή ίση της έναρξης.
     */
    public void setEndDate(LocalDate endDate) {
        if (endDate == null)
            throw new IllegalArgumentException("End date cannot be null.");

        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException("End date cannot be earlier than start date.");

        this.endDate = endDate;
    }

    /**
     * Ορίζει την κατάσταση της ενοικίασης.
     * true = ενεργή, false = ολοκληρωμένη.
     * Ενημερώνει και την κατάσταση του οχήματος.
     */
    public void setStatus(boolean status) {
        this.status = status;
        this.car.setStatus(status);
    }

    //|----------------Λογική--------------|

    /**
     * Ολοκληρώνει την ενοικίαση και καθιστά το όχημα διαθέσιμο.
     */
    public void completeRental() {
        this.status = false;
        this.car.setStatus(false);
    }

    //|----------------Equals & HashCode--------------|

    /**
     * Δύο ενοικιάσεις θεωρούνται ίδιες όταν έχουν τον ίδιο κωδικό.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rental)) return false;
        Rental r = (Rental) o;
        return rentalId.equals(r.rentalId);
    }

    @Override
    public int hashCode() {
        return rentalId == null ? 0 : rentalId.hashCode();
    }

    //|----------------toString--------------|

    /**
     * Επιστρέφει μια συνοπτική περιγραφή της ενοικίασης.
     */
    @Override
    public String toString() {
        return "[" + rentalId + "] "
                + customer.getFullName()
                + " rented " + car.getPlate()
                + " from " + startDate
                + " to " + endDate
                + " (" + (status ? "ACTIVE" : "COMPLETED") + ")";
    }

}