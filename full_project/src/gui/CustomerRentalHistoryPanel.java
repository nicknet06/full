package gui;

import api.entities.Employee;
import api.entities.Rental;
import api.services.CarService;
import api.services.CustomerService;
import api.services.EmployeeService;
import api.services.RentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerRentalHistoryPanel extends JPanel {

    private JTextField txtAfm;
    private JTextField txtPlate;
    private JTable tableResults;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    private CarService carService;
    private EmployeeService employeeService;
    private CustomerService customerService;
    private RentalService rentalService;
    private Employee loggedEmployee;

    private MainFrame mainFrame;

    public CustomerRentalHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Ιστορικό Ενοικιάσεων", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // TOP: Search Panel
        JPanel searchPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Κριτήρια Αναζήτησης"));

        searchPanel.add(new JLabel("ΑΦΜ Πελάτη:"));
        txtAfm = new JTextField(15);
        searchPanel.add(txtAfm);
        JButton btnSearch = new JButton("Αναζήτηση");
        searchPanel.add(btnSearch);

        searchPanel.add(new JLabel("Πινακίδα:"));
        txtPlate = new JTextField(15);
        searchPanel.add(txtPlate);
        JButton btnClear = new JButton("Καθαρισμός");
        searchPanel.add(btnClear);

        // CENTER: Results Panel with Table
        JPanel resultsPanel = new JPanel(new BorderLayout(5, 5));
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Αποτελέσματα"));

        String[] columns = {"ID Ενοικίασης", "Πινακίδα", "ΑΦΜ", "Ημ/νία Έναρξης", "Ημ/νία Λήξης", "Κατάσταση"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableResults = new JTable(tableModel);
        tableResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableResults);
        scrollPane.setPreferredSize(new Dimension(800, 350));
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // BOTTOM: Status and Back
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(lblStatus, BorderLayout.CENTER);

        JButton btnBack = new JButton("Πίσω");
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.add(btnBack);
        bottomPanel.add(backPanel, BorderLayout.SOUTH);

        // Assemble main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Action Listeners
        btnSearch.addActionListener(e -> handleSearch());
        btnClear.addActionListener(e -> handleClear());
        btnBack.addActionListener(e -> goBack());
    }

    public void init(EmployeeService empService, CarService carService,
                     CustomerService customerService, RentalService rentalService,
                     Employee loggedEmployee) {
        this.employeeService = empService;
        this.carService = carService;
        this.customerService = customerService;
        this.rentalService = rentalService;
        this.loggedEmployee = loggedEmployee;
        handleClear();
    }

    private void handleSearch() {
        String afm = txtAfm.getText().trim();
        String plate = txtPlate.getText().trim();

        if (afm.isEmpty() && plate.isEmpty()) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Εισάγετε ΑΦΜ ή Πινακίδα για αναζήτηση.");
            tableModel.setRowCount(0);
            return;
        }

        List<Rental> rentals;

        if (!afm.isEmpty()) {
            rentals = rentalService.getRentalsByCustomer(afm);
        } else {
            rentals = rentalService.getRentalsByCar(plate);
        }

        tableModel.setRowCount(0);
        for (Rental r : rentals) {
            tableModel.addRow(new Object[]{
                    r.getRentalId(),
                    r.getCar().getPlate(),
                    r.getCustomer().getAfm(),
                    r.getStartDate().toString(),
                    r.getEndDate().toString(),
                    r.isActive() ? "Ενεργή" : "Ολοκληρώθηκε"
            });
        }

        if (rentals.isEmpty()) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Δεν βρέθηκαν ενοικιάσεις.");
        } else {
            lblStatus.setForeground(new Color(0, 128, 0));
            lblStatus.setText("Βρέθηκαν " + rentals.size() + " ενοικιάσεις.");
        }
    }

    private void handleClear() {
        txtAfm.setText("");
        txtPlate.setText("");
        tableModel.setRowCount(0);
        lblStatus.setText(" ");
    }

    private void goBack() {
        mainFrame.showMainMenu(employeeService, carService, customerService, rentalService, loggedEmployee);
    }
}

