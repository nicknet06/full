package api.services;

import api.entities.Car;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Υπηρεσία που συγκεντρώνει τη βασική επιχειρησιακή λογική
 * για τη διαχείριση οχημάτων: προσθήκη, αναζήτηση, ενημέρωση,
 * διαγραφή και έλεγχο διαθεσιμότητας.
 *
 * @author Μυρτώ Θεοδουλίδου
 * @author Αγγελική Τσευλίκου
 */
public class CarService {

    private final List<Car> cars = new ArrayList<>();

    /**
     * Προσθέτει ένα νέο όχημα στο σύστημα.
     * Απαιτείται έγκυρο ID και πινακίδα, τα οποία πρέπει να είναι μοναδικά.
     *
     * @param car το όχημα προς καταχώρηση
     * @return true αν η προσθήκη ολοκληρώθηκε, false αν υπάρχει ήδη όχημα
     *         με το ίδιο ID ή την ίδια πινακίδα
     * @throws IllegalArgumentException αν το όχημα ή τα βασικά πεδία του είναι άκυρα
     */
    public boolean addCar(Car car) {

        if (car == null)
            throw new IllegalArgumentException("Car cannot be null!");

        if (car.getId() == null || car.getId().isBlank())
            throw new IllegalArgumentException("Invalid ID!");

        if (car.getPlate() == null || car.getPlate().isBlank())
            throw new IllegalArgumentException("Invalid license plate!");

        // Έλεγχος μοναδικότητας ID
        if (findById(car.getId()) != null)
            return false;

        // Έλεγχος μοναδικότητας πινακίδας
        for (Car c : cars) {
            if (c.getPlate().equalsIgnoreCase(car.getPlate())) {
                return false;
            }
        }

        cars.add(car);
        return true;
    }

    /**
     * Αναζητά όχημα με βάση το μοναδικό ID του.
     *
     * @param id το ID προς αναζήτηση
     * @return το αντίστοιχο όχημα ή null αν δεν βρεθεί
     */
    public Car findById(String id) {
        if (id == null || id.isBlank()) return null;

        id = id.trim();
        if (id.isEmpty()) return null;

        for (Car c : cars) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Αναζητά όχημα με βάση την πινακίδα κυκλοφορίας.
     *
     * @param plate η πινακίδα προς αναζήτηση
     * @return το όχημα ή null αν δεν υπάρχει
     */
    public Car findByPlate(String plate) {
        if (plate == null || plate.isBlank()) {
            return null;
        }

        plate = plate.trim();
        if (plate.isEmpty()) {
            return null;
        }

        for (Car c : cars) {
            if (c.getPlate() != null && c.getPlate().equalsIgnoreCase(plate)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Ενημερώνει τα στοιχεία ενός υπάρχοντος οχήματος.
     * Πραγματοποιεί ελέγχους εγκυρότητας και πιθανές συγκρούσεις
     * (π.χ. αλλαγή πινακίδας σε ήδη χρησιμοποιούμενη).
     *
     * @param id το ID του οχήματος προς ενημέρωση
     * @param newCarData αντικείμενο Car με τα νέα δεδομένα
     */
    public void updateCar(String id, Car newCarData) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Id cannot be null or empty.");

        if (newCarData == null)
            throw new IllegalArgumentException("New data cannot be null.");

        // Έλεγχοι βασικών πεδίων
        if (newCarData.getPlate() == null || newCarData.getPlate().isBlank())
            throw new IllegalArgumentException("Invalid license plate!");

        if (newCarData.getBrand() == null || newCarData.getBrand().isBlank())
            throw new IllegalArgumentException("Brand cannot be empty!");

        if (newCarData.getModel() == null || newCarData.getModel().isBlank())
            throw new IllegalArgumentException("Model cannot be empty!");

        if (newCarData.getType() == null || newCarData.getType().isBlank())
            throw new IllegalArgumentException("Type cannot be empty!");

        int currentYear = LocalDate.now().getYear();
        if (newCarData.getYear() < 1900 || newCarData.getYear() > currentYear)
            throw new IllegalArgumentException("Invalid manufacturing year: " + newCarData.getYear());

        if (newCarData.getColor() == null || newCarData.getColor().isBlank())
            throw new IllegalArgumentException("Color cannot be empty!");

        // Εύρεση υπάρχοντος οχήματος
        Car existing = findById(id);
        if (existing == null)
            throw new IllegalArgumentException("Car with id " + id + " does not exist.");

        // Έλεγχος σύγκρουσης πινακίδας
        if (!existing.getPlate().equalsIgnoreCase(newCarData.getPlate())) {
            Car carWithSamePlate = findByPlate(newCarData.getPlate());
            if (carWithSamePlate != null && carWithSamePlate != existing) {
                throw new IllegalArgumentException("License plate already in use by another car.");
            }
        }

        // Ενημέρωση πεδίων
        existing.setPlate(newCarData.getPlate());
        existing.setBrand(newCarData.getBrand());
        existing.setModel(newCarData.getModel());
        existing.setType(newCarData.getType());
        existing.setYear(newCarData.getYear());
        existing.setColor(newCarData.getColor());
        existing.setStatus(newCarData.getStatus());
    }

    /**
     * Διαγράφει όχημα από το σύστημα με βάση το ID του.
     *
     * @param id το ID του οχήματος
     */
    public void deleteCar(String id) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Id cannot be null or empty.");

        id = id.trim();

        Car existing = findById(id);
        if (existing == null)
            throw new IllegalArgumentException("Car with id " + id + " does not exist.");

        cars.remove(existing);
    }

    /**
     * Επιστρέφει όλα τα οχήματα του συστήματος.
     */
    public List<Car> getAllCars() {
        return cars;
    }

    /**
     * Αναζήτηση οχημάτων με πολλαπλά προαιρετικά κριτήρια.
     * Κενές τιμές αγνοούνται. Αν το status είναι null, δεν χρησιμοποιείται ως φίλτρο.
     */
    public ArrayList<Car> searchCars(
            String brand,
            String plate,
            String model,
            String color,
            Boolean status
    ) {
        ArrayList<Car> results = new ArrayList<>();

        for (Car c : cars) {

            if (brand != null && !brand.isBlank()) {
                if (c.getBrand() == null || !c.getBrand().equalsIgnoreCase(brand.trim()))
                    continue;
            }

            if (plate != null && !plate.isBlank()) {
                if (c.getPlate() == null || !c.getPlate().equalsIgnoreCase(plate.trim()))
                    continue;
            }

            if (model != null && !model.isBlank()) {
                if (c.getModel() == null || !c.getModel().equalsIgnoreCase(model.trim()))
                    continue;
            }

            if (color != null && !color.isBlank()) {
                if (c.getColor() == null || !c.getColor().equalsIgnoreCase(color.trim()))
                    continue;
            }

            if (status != null) {
                if (c.getStatus() != status)
                    continue;
            }

            results.add(c);
        }

        return results;
    }

    /**
     * Ελέγχει αν ένα όχημα είναι διαθέσιμο προς ενοικίαση.
     *
     * @param id το ID του οχήματος
     * @return true αν υπάρχει και είναι διαθέσιμο, αλλιώς false
     */
    public boolean isCarAvailable(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }

        id = id.trim();

        Car c = findById(id);
        if (c == null) {
            return false;
        }

        return c.getStatus();
    }

    /**
     * Αλλάζει τη διαθεσιμότητα ενός οχήματος.
     *
     * @param id το ID του οχήματος
     * @param available true για διαθέσιμο, false για ενοικιασμένο
     */
    public void setCarStatus(String id, boolean available) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Id cannot be null or blank.");

        id = id.trim();

        Car c = findById(id);
        if (c == null)
            throw new IllegalArgumentException("Car with id " + id + " does not exist.");

        c.setStatus(available);
    }

}