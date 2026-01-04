package api.entities;

/**
 * Αντικείμενο που περιγράφει ένα όχημα του συστήματος ενοικιάσεων.
 * <p>
 * Κάθε αυτοκίνητο διαθέτει μοναδικό αναγνωριστικό, πινακίδα,
 * βασικά τεχνικά χαρακτηριστικά (μάρκα, μοντέλο, τύπο, έτος κατασκευής),
 * χρώμα και πληροφορία σχετικά με τη διαθεσιμότητά του.
 * </p>
 * @author Μυρτώ Θεοδουλίδου
 * @author Αγγελική Τσευλίκου
 */
public class Car {

    //|----------------Μεταβλητές κατάστασης--------------|

    private String id;        // Μοναδικό αναγνωριστικό οχήματος
    private String plate;     // Πινακίδα κυκλοφορίας
    private String brand;     // Μάρκα κατασκευαστή
    private String type;      // Κατηγορία (π.χ. Sedan, SUV)
    private String model;     // Συγκεκριμένο μοντέλο
    private int year;         // Έτος παραγωγής
    private String color;     // Χρώμα οχήματος
    private boolean status;   // true = διαθέσιμο, false = ενοικιασμένο

    //|----------------Constructor--------------|

    /**
     * Δημιουργεί ένα νέο αντικείμενο Car με όλα τα απαραίτητα στοιχεία.
     *
     * @param id        μοναδικό ID
     * @param plate     πινακίδα κυκλοφορίας
     * @param brand     μάρκα
     * @param model     μοντέλο
     * @param type      τύπος/κατηγορία
     * @param year      έτος κατασκευής
     * @param color     χρώμα
     * @param status    τρέχουσα κατάσταση διαθεσιμότητας
     *
     * @throws IllegalArgumentException αν το έτος δεν βρίσκεται σε αποδεκτό εύρος
     */
    public Car(String id, String plate, String brand, String model,
               String type, int year, String color, boolean status){

        this.id = id;
        this.plate = plate;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.color = color;
        this.status = status;

        // Έλεγχος εγκυρότητας έτους παραγωγής
        int currentYear = java.time.LocalDate.now().getYear();
        if (year < 1950 || year > currentYear) {
            throw new IllegalArgumentException("Invalid manufacturing year: " + year);
        }

        this.year = year;
    }

    //|----------------Getters--------------|

    public String getId()    { return id; }
    public String getPlate() { return plate; }
    public String getBrand() { return brand; }
    public String getType()  { return type; }
    public String getModel() { return model; }
    public int getYear()     { return year; }
    public String getColor() { return color; }
    public boolean getStatus() { return status; }

    //|----------------Setters--------------|

    public void setId(String id) {
        this.id = id;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Ορίζει το έτος κατασκευής του οχήματος.
     *
     * @param year έτος παραγωγής (1950 έως τρέχον έτος)
     */
    public void setYear(int year) {
        int currentYear = java.time.LocalDate.now().getYear();
        if (year < 1950 || year > currentYear) {
            throw new IllegalArgumentException("Invalid manufacturing year: " + year);
        }
        this.year = year;
    }

    //|----------------Equals & HashCode--------------|

    /**
     * Δύο οχήματα θεωρούνται ίδια όταν μοιράζονται το ίδιο μοναδικό ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return id.equals(car.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    //|----------------toString--------------|

    /**
     * Επιστρέφει μια συνοπτική περιγραφή του οχήματος.
     */
    @Override
    public String toString() {
        return "[" + id + "] " +
                plate + " - " +
                brand + " " +
                model + " (" + color + ") - " +
                (status ? "Διαθέσιμο" : "Ενοικιασμένο");
    }

}