import api.entities.Car;
import api.entities.Rental;
import api.services.*;
import api.repository.CsvLoader;
import api.repository.FileStorage;
import gui.MainFrame;

import javax.swing.*;

public class Main {

    private final EmployeeService employeeService = new EmployeeService();
    private final CarService carService = new CarService();
    private final CustomerService customerService = new CustomerService();
    private final RentalService rentalService = new RentalService();

    private final CsvLoader loader = new CsvLoader();
    private final FileStorage storage = new FileStorage();

    public void start() {
        // 1) Φόρτωση αποθηκευμένων δεδομένων αν υπάρχουν
        try {
            if (storage.hasSavedState()) {
                storage.loadEmployees(employeeService);
                storage.loadCars(carService);
                storage.loadCustomers(customerService);
                storage.loadRentals(rentalService, carService, customerService, employeeService);
            } else {
                loader.loadEmployees(employeeService);
                loader.loadCars(carService);
                loader.loadCustomers(customerService);
                loader.loadRentals(rentalService, carService, customerService, employeeService);

                storage.saveEmployees(employeeService);
                storage.saveCars(carService);
                storage.saveCustomers(customerService);
                storage.saveRentals(rentalService);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        syncCarStatusesFromRentals();

        // 3) Εκκίνηση Swing UI
        SwingUtilities.invokeLater(() -> {
            try {
                // Ορισμός Look and Feel στο προεπιλεγμένο του συστήματος
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainFrame mainFrame = new MainFrame();
            mainFrame.initLogin(employeeService, carService, customerService, rentalService);

            // Προσθήκη shutdown hook για αποθήκευση δεδομένων κατά το κλείσιμο της εφαρμογής
            Runtime.getRuntime().addShutdownHook(new Thread(this::saveData));

            mainFrame.setVisible(true);
        });
    }

    /**
     * Εξασφαλίζει ότι η διαθεσιμότητα των αυτοκινήτων ταιριάζει με τις ενοικιάσεις:
     * - Αν ένα αυτοκίνητο έχει ΕΝΕΡΓΗ ενοικίαση -> ΕΝΟΙΚΙΑΣΜΕΝΟ (false)
     * - Αλλιώς -> ΔΙΑΘΕΣΙΜΟ (true)
     */
    private void syncCarStatusesFromRentals() {
        // πρώτα ορισμός όλων ως διαθέσιμα (true = διαθέσιμο)
        for (Car c : carService.getAllCars()) {
            c.setStatus(true);
        }

        // ορισμός ως ενοικιασμένo αν υπάρχει ενεργή ενοικίαση (false = ενοικιασμένο)
        for (Rental r : rentalService.getAllRentals()) {
            if (r.isActive()) {
                r.getCar().setStatus(false);
            }
        }
    }

    private void saveData() {
        try {
            storage.saveEmployees(employeeService);
            storage.saveCars(carService);
            storage.saveCustomers(customerService);
            storage.saveRentals(rentalService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main().start();
    }
}
