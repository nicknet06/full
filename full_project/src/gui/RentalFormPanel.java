package gui;

import api.entities.Car;
import api.entities.Customer;
import api.entities.Employee;
import api.services.CarService;
import api.services.CustomerService;
import api.services.EmployeeService;
import api.services.RentalService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RentalFormPanel extends JPanel {

    private JComboBox<Car> carBox;
    private JComboBox<Customer> customerBox;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextField txtCustomerAfm;
    private JLabel lblStatus;

    private CarService carService;
    private EmployeeService employeeService;
    private CustomerService customerService;
    private RentalService rentalService;
    private Employee loggedEmployee;

    private MainFrame mainFrame;

    public RentalFormPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Νέα Ενοικίαση", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main Panel with BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Car Selection Panel
        JPanel carPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        carPanel.add(new JLabel("Αυτοκίνητο:"));
        carBox = new JComboBox<>();
        carBox.setPreferredSize(new Dimension(350, 28));
        carPanel.add(carBox);
        mainPanel.add(carPanel);

        // Customer Selection Panel
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        customerPanel.add(new JLabel("Πελάτης:"));
        customerBox = new JComboBox<>();
        customerBox.setPreferredSize(new Dimension(350, 28));
        customerPanel.add(customerBox);
        mainPanel.add(customerPanel);

        // AFM Search Panel
        JPanel afmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        afmPanel.add(new JLabel("Αναζήτηση ΑΦΜ:"));
        txtCustomerAfm = new JTextField(20);
        JButton btnFindCustomer = new JButton("Εύρεση");
        afmPanel.add(txtCustomerAfm);
        afmPanel.add(btnFindCustomer);
        mainPanel.add(afmPanel);

        // Date Panel
        JPanel datePanel = new JPanel(new GridLayout(2, 2, 15, 15));
        datePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        datePanel.add(new JLabel("Ημ/νία Έναρξης (ΕΕΕΕ-ΜΜ-ΗΗ):"));
        txtStartDate = new JTextField(15);
        datePanel.add(txtStartDate);
        datePanel.add(new JLabel("Ημ/νία Λήξης (ΕΕΕΕ-ΜΜ-ΗΗ):"));
        txtEndDate = new JTextField(15);
        datePanel.add(txtEndDate);
        mainPanel.add(datePanel);

        // Status Label
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(Color.RED);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        mainPanel.add(lblStatus);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton btnCreate = new JButton("Δημιουργία Ενοικίασης");
        JButton btnClear = new JButton("Καθαρισμός");
        JButton btnBack = new JButton("Πίσω");

        btnCreate.setPreferredSize(new Dimension(180, 35));
        btnClear.setPreferredSize(new Dimension(120, 35));
        btnBack.setPreferredSize(new Dimension(120, 35));

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);
        mainPanel.add(buttonPanel);

        // Center the form
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.add(mainPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // Action Listeners
        btnCreate.addActionListener(e -> handleCreateRental());
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> goBack());
        btnFindCustomer.addActionListener(e -> handleFindCustomer());
    }

    public void init(EmployeeService empService, CarService carService,
                     CustomerService customerService, RentalService rentalService,
                     Employee loggedEmployee) {
        this.employeeService = empService;
        this.carService = carService;
        this.customerService = customerService;
        this.rentalService = rentalService;
        this.loggedEmployee = loggedEmployee;
        loadData();
        clearFields();
    }

    private void loadData() {
        carBox.removeAllItems();
        customerBox.removeAllItems();

        // Load available cars
        List<Car> availableCars = carService.getAllCars().stream()
                .filter(c -> c.getStatus())
                .toList();
        for (Car car : availableCars) {
            carBox.addItem(car);
        }

        // Load all customers
        for (Customer customer : customerService.getAllCustomers()) {
            customerBox.addItem(customer);
        }
    }

    private void handleCreateRental() {
        Car car = (Car) carBox.getSelectedItem();
        Customer cust = (Customer) customerBox.getSelectedItem();
        String startStr = txtStartDate.getText().trim();
        String endStr = txtEndDate.getText().trim();

        if (car == null || cust == null || startStr.isEmpty() || endStr.isEmpty()) {
            lblStatus.setText("Συμπληρώστε όλα τα πεδία!");
            return;
        }

        LocalDate start, end;
        try {
            start = LocalDate.parse(startStr);
            end = LocalDate.parse(endStr);
        } catch (DateTimeParseException e) {
            lblStatus.setText("Μη έγκυρη μορφή ημερομηνίας! Χρησιμοποιήστε ΕΕΕΕ-ΜΜ-ΗΗ");
            return;
        }

        boolean success = rentalService.rentCar(car, cust, loggedEmployee, start, end);

        if (success) {
            lblStatus.setForeground(new Color(0, 128, 0));
            lblStatus.setText("Η ενοικίαση δημιουργήθηκε επιτυχώς.");
            clearFields();
            loadData(); // refresh available cars
        } else {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Η ενοικίαση δεν μπόρεσε να δημιουργηθεί.");
        }
    }

    private void handleFindCustomer() {
        String afm = txtCustomerAfm.getText().trim();

        if (afm.isEmpty()) {
            lblStatus.setText("Δώστε ΑΦΜ για αναζήτηση.");
            return;
        }

        Customer found = customerService.findByAfm(afm);
        if (found != null) {
            customerBox.setSelectedItem(found);
            lblStatus.setForeground(new Color(0, 128, 0));
            lblStatus.setText("Πελάτης βρέθηκε: " + found.getFullName());
        } else {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Πελάτης δεν βρέθηκε με ΑΦΜ: " + afm);
        }
    }

    private void clearFields() {
        if (carBox.getItemCount() > 0) carBox.setSelectedIndex(0);
        if (customerBox.getItemCount() > 0) customerBox.setSelectedIndex(0);
        txtStartDate.setText("");
        txtEndDate.setText("");
        txtCustomerAfm.setText("");
        lblStatus.setText(" ");
        lblStatus.setForeground(Color.RED);
    }

    private void goBack() {
        mainFrame.showMainMenu(employeeService, carService, customerService, rentalService, loggedEmployee);
    }
}
