package api.repository;

import api.entities.*;
import api.services.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Κλάση υπεύθυνη για την επίμονη αποθήκευση των δεδομένων της εφαρμογής.
 * <p>
 * Όλα τα CSV αρχεία δημιουργούνται σε έναν φάκελο μέσα στο user.home,
 * ώστε η εφαρμογή να μπορεί να επαναφέρει την προηγούμενη κατάσταση
 * χωρίς να βασίζεται στα resources.
 * </p>
 *
 * <p>
 * Η αποθήκευση και φόρτωση καλύπτει:
 * <ul>
 *     <li>Υπαλλήλους</li>
 *     <li>Πελάτες</li>
 *     <li>Οχήματα (με boolean κατάσταση διαθεσιμότητας)</li>
 *     <li>Ενοικιάσεις (με boolean ενεργή/ολοκληρωμένη)</li>
 * </ul>
 * </p>
 * @author
 *     Μυρτώ Θεοδουλίδου
 *     Αγγελική Τσευλίκου
 */
public class FileStorage {

    /* =============================================================
       ΡΥΘΜΙΣΕΙΣ ΑΠΟΘΗΚΕΥΣΗΣ
       Κεντρικός φάκελος όπου δημιουργούνται όλα τα CSV αρχεία.
       ============================================================= */
    private static final String BASE_DIR =
            Paths.get(System.getProperty("user.dir"), "data").toString();

    public FileStorage() {
        /* Δημιουργία φακέλου αν δεν υπάρχει */
        new File(BASE_DIR).mkdirs();
    }

    /* =============================================================
       ΒΟΗΘΗΤΙΚΕΣ ΜΕΘΟΔΟΙ IO
       ============================================================= */

    /** Επιστρέφει το Path ενός αρχείου μέσα στο BASE_DIR. */
    private Path path(String fileName) {
        System.out.println("Accessing file: " + Paths.get(BASE_DIR, fileName).toString());
        return Paths.get(BASE_DIR, fileName);
    }

    /** Ελέγχει αν υπάρχει το συγκεκριμένο αρχείο. */
    private boolean exists(String fileName) {
        return Files.exists(path(fileName));
    }

    /** Δημιουργεί writer για εγγραφή CSV αρχείων. */
    private BufferedWriter writer(String fileName) throws IOException {
        return Files.newBufferedWriter(
                path(fileName),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    /** Δημιουργεί reader για ανάγνωση CSV αρχείων. */
    private BufferedReader reader(String fileName) throws IOException {
        return Files.newBufferedReader(path(fileName), StandardCharsets.UTF_8);
    }

    /**
     * Ελέγχει αν υπάρχουν όλα τα αρχεία αποθήκευσης.
     * <p>
     * Αν ισχύει, η εφαρμογή φορτώνει δεδομένα από FileStorage
     * αντί για τα αρχικά resources.
     * </p>
     */
    public boolean hasSavedState() {
        return exists("employees.csv")
                && exists("cars.csv")
                && exists("customers.csv")
                && exists("rentals.csv");
    }

    /* =============================================================
       ΑΠΟΘΗΚΕΥΣΗ ΔΕΔΟΜΕΝΩΝ
       ============================================================= */

    /** Αποθήκευση υπαλλήλων σε CSV. */
    public void saveEmployees(EmployeeService service) throws IOException {
        try (BufferedWriter bw = writer("employees.csv")) {
            bw.write("name,surname,username,email,password\n");

            for (Employee e : service.getAllEmployees()) {
                String[] nameParts = e.getFullName().split(" ", 2);
                String name = nameParts.length > 0 ? nameParts[0] : "";
                String surname = nameParts.length > 1 ? nameParts[1] : "";
                bw.write(
                        escape(name) + "," +
                        escape(surname) + "," +
                        escape(e.getUsername()) + "," +
                        escape(e.getEmail()) + "," +
                        escape(e.getPassword()) + "\n"
                );
            }
        }
    }

    /** Αποθήκευση οχημάτων με string κατάσταση διαθεσιμότητας. */
    public void saveCars(CarService service) throws IOException {
        try (BufferedWriter bw = writer("cars.csv")) {
            bw.write("id,plate,brand,type,model,year,color,status\n");

            for (Car c : service.getAllCars()) {
                String status = c.getStatus() ? "Διαθέσιμο" : "Ενοικιασμένο";
                bw.write(
                        escape(c.getId()) + "," +
                        escape(c.getPlate()) + "," +
                        escape(c.getBrand()) + "," +
                        escape(c.getType()) + "," +
                        escape(c.getModel()) + "," +
                        c.getYear() + "," +
                        escape(c.getColor()) + "," +
                        status + "\n"
                );
            }
        }
    }

    /** Αποθήκευση πελατών. */
    public void saveCustomers(CustomerService service) throws IOException {
        try (BufferedWriter bw = writer("customers.csv")) {
            bw.write("afm,fullName,phone,email\n");

            for (Customer c : service.getAllCustomers()) {
                bw.write(
                        escape(c.getAfm()) + "," +
                                escape(c.getFullName()) + "," +
                                escape(c.getPhoneNumber()) + "," +
                                escape(c.getEmail()) + "\n"
                );
            }
        }
    }

    /**
     * Αποθήκευση ενοικιάσεων.
     * <p>
     * Στο project του φίλου σου:
     * <ul>
     *     <li>true  → ενεργή</li>
     *     <li>false → ολοκληρωμένη</li>
     * </ul>
     * </p>
     */
    public void saveRentals(RentalService service) throws IOException {
        try (BufferedWriter bw = writer("rentals.csv")) {
            bw.write("rentalId,carId,afm,username,startDate,endDate,active\n");

            for (Rental r : service.getAllRentals()) {
                bw.write(
                        escape(r.getRentalId()) + "," +
                                escape(r.getCar().getId()) + "," +
                                escape(r.getCustomer().getAfm()) + "," +
                                escape(r.getEmployee().getUsername()) + "," +
                                r.getStartDate() + "," +
                                r.getEndDate() + "," +
                                r.isActive() + "\n"   /* boolean status */
                );
            }
        }
    }

    /* =============================================================
       ΦΟΡΤΩΣΗ ΔΕΔΟΜΕΝΩΝ
       ============================================================= */

    /** Φόρτωση υπαλλήλων από CSV. */
    public void loadEmployees(EmployeeService service) throws IOException {
        if (!exists("employees.csv")) return;

        try (BufferedReader br = reader("employees.csv")) {
            br.readLine(); /* header */

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",", -1);
                if (d.length < 5) continue;

                String fullName = d[0].trim() + (d[1].trim().isEmpty() ? "" : (" " + d[1].trim()));
                Employee e = new Employee(
                        fullName,
                        d[2].trim(),
                        d[3].trim(),
                        d[4].trim()
                );

                service.addEmployee(e);
            }
        }
    }

    /** Φόρτωση οχημάτων με string κατάσταση διαθεσιμότητας. */
    public void loadCars(CarService service) throws IOException {
        if (!exists("cars.csv")) return;

        try (BufferedReader br = reader("cars.csv")) {
            br.readLine(); /* header */

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",", -1);
                if (d.length < 8) continue;

                boolean available = d[7].trim().equalsIgnoreCase("Διαθέσιμο");

                Car car = new Car(
                        d[0].trim(),
                        d[1].trim(),
                        d[2].trim(),
                        d[4].trim(), // model
                        d[3].trim(), // type
                        Integer.parseInt(d[5].trim()),
                        d[6].trim(),
                        available
                );

                service.addCar(car);
            }
        }
    }

    /** Φόρτωση πελατών από CSV. */
    public void loadCustomers(CustomerService service) throws IOException {
        if (!exists("customers.csv")) return;

        try (BufferedReader br = reader("customers.csv")) {
            br.readLine(); /* header */

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",", -1);
                if (d.length < 4) continue;

                Customer c = new Customer(
                        d[0].trim(),
                        d[1].trim(),
                        d[2].trim(),
                        d[3].trim()
                );

                service.addCustomer(c);
            }
        }
    }

    /**
     * Φόρτωση ενοικιάσεων.
     * <p>
     * Το status είναι boolean:
     * <ul>
     *     <li>true  → ενεργή</li>
     *     <li>false → ολοκληρωμένη</li>
     * </ul>
     * </p>
     */
    public void loadRentals(
            RentalService rentalService,
            CarService carService,
            CustomerService customerService,
            EmployeeService employeeService
    ) throws IOException {

        if (!exists("rentals.csv")) return;

        try (BufferedReader br = reader("rentals.csv")) {
            br.readLine(); /* header */

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] d = line.split(",", -1);
                if (d.length < 7) continue;

                String rentalId = d[0].trim();
                String carId    = d[1].trim();
                String afm      = d[2].trim();
                String username = d[3].trim();

                var start = java.time.LocalDate.parse(d[4].trim());
                var end   = java.time.LocalDate.parse(d[5].trim());

                boolean active = Boolean.parseBoolean(d[6].trim());

                Car car = carService.findById(carId);
                Customer cust = customerService.findByAfm(afm);
                Employee emp = employeeService.findByUsername(username);

                /* Αν λείπει κάποιο βασικό στοιχείο → αγνόηση γραμμής */
                if (car == null || cust == null || emp == null) continue;

                Rental rental = new Rental(rentalId, car, cust, emp, start, end);
                rental.setStatus(active); /* boolean */

                rentalService.getAllRentals().add(rental);
            }
        }
    }

    /* =============================================================
       UTIL
       ============================================================= */

    /** Αντικαθιστά κόμματα ώστε να μην αλλοιωθεί η δομή του CSV. */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }
}