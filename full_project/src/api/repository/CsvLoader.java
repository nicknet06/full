package api.repository;

import api.entities.*;
import api.services.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Κλάση υπεύθυνη για τη φόρτωση αρχικών δεδομένων από CSV αρχεία
 * που βρίσκονται μέσα στο resources.
 * <p>
 * Η φόρτωση περιλαμβάνει υπαλλήλους, πελάτες, οχήματα και ενοικιάσεις.
 * Τα δεδομένα διαβάζονται χωρίς εφαρμογή επιχειρησιακών κανόνων,
 * καθώς ο σκοπός της κλάσης είναι αποκλειστικά η εισαγωγή τους
 * στο σύστημα κατά την εκκίνηση.
 * </p>
 *
 * <p>
 * Στο project αυτό, η κατάσταση των οχημάτων και των ενοικιάσεων
 * εκφράζεται με boolean τιμές:
 * <ul>
 *     <li>true  → διαθέσιμο / ενεργή</li>
 *     <li>false → ενοικιασμένο / ολοκληρωμένη</li>
 * </ul>
 * </p>
 *
 * @author
 *     Μυρτώ Θεοδουλίδου
 *     Αγγελική Τσευλίκου
 */
public class CsvLoader {

    /**
     * Ανοίγει ένα CSV αρχείο από τον φάκελο resources και επιστρέφει
     * έναν BufferedReader για ανάγνωση. Αν το αρχείο δεν εντοπιστεί,
     * δημιουργείται εξαίρεση με σχετικό μήνυμα.
     */
    private BufferedReader openCsv(String path) throws IOException {
        InputStream in = CsvLoader.class.getResourceAsStream(path);
        if (in == null) {
            throw new IOException("Το αρχείο δεν βρέθηκε: " + path);
        }
        return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    /* =============================================================
       ΥΠΑΛΛΗΛΟΙ
       users.csv → name,surname,username,email,password
       ============================================================= */
    public void loadEmployees(EmployeeService service) throws IOException {
        try (BufferedReader br = openCsv("/data/users.csv")) {

            br.readLine(); /* παράλειψη επικεφαλίδας */

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",");
                if (d.length < 5) continue;

                Employee emp = new Employee(
                        d[0].trim() + " " + d[1].trim(), /* ονοματεπώνυμο */
                        d[2].trim(),                     /* username */
                        d[3].trim(),                     /* email */
                        d[4].trim()                      /* password */
                );

                service.addEmployee(emp);
            }
        }
    }

    /* =============================================================
       ΠΕΛΑΤΕΣ
       customers.csv → afm,fullName,phone,email
       ============================================================= */
    public void loadCustomers(CustomerService service) throws IOException {
        loadCustomersFrom("/data/customers.csv", service);
    }

    /**
     * Φορτώνει πελάτες από συγκεκριμένο resource path.
     */
    public void loadCustomersFrom(String path, CustomerService service) throws IOException {
        try (BufferedReader br = openCsv(path)) {

            br.readLine(); /* header */

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",");
                if (d.length < 4) continue;

                Customer c = new Customer(
                        d[0].trim(), /* afm */
                        d[1].trim(), /* όνομα */
                        d[2].trim(), /* τηλέφωνο */
                        d[3].trim()  /* email */
                );

                service.addCustomer(c);
            }
        }
    }

    /* =============================================================
       ΟΧΗΜΑΤΑ
       vehicles_with_plates.csv
       id,plate,brand,type,model,year,color,status

       status = true (διαθέσιμο), false (ενοικιασμένο)
       ============================================================= */
    public void loadCars(CarService service) throws IOException {
        try (BufferedReader br = openCsv("/data/vehicles_with_plates.csv")) {

            br.readLine(); /* skip header */

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",");
                if (d.length < 8) continue;

                /* Μετατροπή ελληνικού status σε boolean */
                boolean available = d[7].trim().equalsIgnoreCase("Διαθέσιμο");

                Car car = new Car(
                        d[0].trim(),                    /* id */
                        d[1].trim(),                    /* plate */
                        d[2].trim(),                    /* brand */
                        d[4].trim(),                    /* model */
                        d[3].trim(),                    /* type */
                        Integer.parseInt(d[5].trim()), /* year */
                        d[6].trim(),                    /* color */
                        available                       /* boolean status */
                );

                service.addCar(car);
            }
        }
    }

    /* =============================================================
       ΕΝΟΙΚΙΑΣΕΙΣ
       rentals.csv
       rentalId,carId,afm,username,startDate,endDate,status

       status = true (ενεργή), false (ολοκληρωμένη)
       ============================================================= */
    public void loadRentals(
            RentalService rentalService,
            CarService carService,
            CustomerService customerService,
            EmployeeService employeeService
    ) throws IOException {

        try (BufferedReader br = openCsv("/data/rentals.csv")) {

            br.readLine(); /* skip header */

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",", -1);
                if (d.length < 7) continue;

                String rentalId = d[0].trim();
                String carId    = d[1].trim();
                String afm      = d[2].trim();
                String username = d[3].trim();
                LocalDate start = LocalDate.parse(d[4].trim());
                LocalDate end   = LocalDate.parse(d[5].trim());

                /* Μετατροπή status σε boolean */
                boolean active = d[6].trim().equalsIgnoreCase("ACTIVE");

                Car car = carService.findById(carId);
                Customer cust = customerService.findByAfm(afm);
                Employee emp = employeeService.findByUsername(username);

                /* Αν λείπει κάποιο βασικό στοιχείο → αγνόηση γραμμής */
                if (car == null || cust == null || emp == null) continue;

                Rental rental = new Rental(rentalId, car, cust, emp, start, end);
                rental.setStatus(active); /* boolean status */

                /* Προσθήκη χωρίς validations (raw import) */
                rentalService.getAllRentals().add(rental);
            }
        }
    }
}