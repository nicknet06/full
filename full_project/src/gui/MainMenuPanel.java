package gui;

import api.entities.Employee;
import api.services.CarService;
import api.services.CustomerService;
import api.services.EmployeeService;
import api.services.RentalService;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    private EmployeeService employeeService;
    private CarService carService;
    private CustomerService customerService;
    private RentalService rentalService;
    private Employee loggedEmployee;

    private MainFrame mainFrame;

    public MainMenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title
        JLabel titleLabel = new JLabel("Σύστημα Διαχείρισης Ενοικίασεων Αυτοκινήτων", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Button Panel - 2 columns layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 2, 20, 20));

        JButton btnCars = new JButton("Διαχείριση Αυτοκινήτων");
        JButton btnCustomers = new JButton("Διαχείριση Πελατών");
        JButton btnEmployees = new JButton("Διαχείριση Υπαλλήλων");
        JButton btnNewRental = new JButton("Νέα Ενοικίαση");
        JButton btnReturnRental = new JButton("Επιστροφή Ενοικίασης");
        JButton btnHistory = new JButton("Ιστορικό Ενοικιάσεων");
        JButton btnLogout = new JButton("Αποσύνδεση");

        Dimension buttonSize = new Dimension(220, 50);
        btnCars.setPreferredSize(buttonSize);
        btnCustomers.setPreferredSize(buttonSize);
        btnEmployees.setPreferredSize(buttonSize);
        btnNewRental.setPreferredSize(buttonSize);
        btnReturnRental.setPreferredSize(buttonSize);
        btnHistory.setPreferredSize(buttonSize);
        btnLogout.setPreferredSize(buttonSize);

        buttonPanel.add(btnCars);
        buttonPanel.add(btnCustomers);
        buttonPanel.add(btnNewRental);
        buttonPanel.add(btnReturnRental);
        buttonPanel.add(btnEmployees);
        buttonPanel.add(btnHistory);
        buttonPanel.add(btnLogout);
        buttonPanel.add(new JLabel("")); // Empty cell for balance

        // Center the button panel
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.add(buttonPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // Action Listeners
        btnCars.addActionListener(e -> openCars());
        btnCustomers.addActionListener(e -> openCustomers());
        btnEmployees.addActionListener(e -> openEmployees());
        btnNewRental.addActionListener(e -> openRentalForm());
        btnReturnRental.addActionListener(e -> openReturnRental());
        btnHistory.addActionListener(e -> openHistory());
        btnLogout.addActionListener(e -> logout());
    }

    public void init(EmployeeService empService,
                     CarService carService,
                     CustomerService customerService,
                     RentalService rentalService,
                     Employee loggedEmployee) {
        this.employeeService = empService;
        this.carService = carService;
        this.customerService = customerService;
        this.rentalService = rentalService;
        this.loggedEmployee = loggedEmployee;
    }

    private void openCars() {
        mainFrame.showCarForm(employeeService, carService, customerService, rentalService, loggedEmployee);
    }

    private void openCustomers() {
        mainFrame.showCustomerForm(employeeService, carService, customerService, rentalService, loggedEmployee);
    }

    private void openEmployees() {
        mainFrame.showEmployeeForm(employeeService, carService, customerService, rentalService, loggedEmployee);
    }

    private void openRentalForm() {
        mainFrame.showRentalForm(employeeService, carService, customerService, rentalService, loggedEmployee);
    }

    private void openReturnRental() {
        mainFrame.showReturnRental(employeeService, carService, customerService, rentalService, loggedEmployee);
    }

    private void openHistory() {
        mainFrame.showHistory(employeeService, carService, customerService, rentalService, loggedEmployee);
    }

    private void logout() {
        mainFrame.showLogin(employeeService, carService, customerService, rentalService);
    }
}
