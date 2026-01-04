package gui;

import api.entities.Customer;
import api.entities.Employee;
import api.services.CarService;
import api.services.CustomerService;
import api.services.EmployeeService;
import api.services.RentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerFormPanel extends JPanel {

    private JTable tableCustomers;
    private DefaultTableModel tableModel;

    private JTextField txtAfm;
    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtEmail;

    private JLabel lblStatus;

    private CarService carService;
    private EmployeeService employeeService;
    private CustomerService customerService;
    private RentalService rentalService;
    private Employee loggedEmployee;

    private MainFrame mainFrame;

    public CustomerFormPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Διαχείριση Πελατών", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // LEFT: Form Panel
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridy = 0;
        gbc.gridx = 0;
        formPanel.add(new JLabel("ΑΦΜ:"), gbc);
        txtAfm = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtAfm, gbc);

        // Row 2
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Ονοματεπώνυμο:"), gbc);
        txtFullName = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtFullName, gbc);

        // Row 3
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Τηλέφωνο:"), gbc);
        txtPhone = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        // Row 4
        gbc.gridy = 3;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 10));
        JButton btnAdd = new JButton("Προσθήκη");
        JButton btnUpdate = new JButton("Ενημέρωση");
        JButton btnDelete = new JButton("Διαγραφή");
        JButton btnSearch = new JButton("Αναζήτηση");
        JButton btnClear = new JButton("Καθαρισμός");
        JButton btnBack = new JButton("Πίσω");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);

        // Status Label
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(Color.BLUE);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));

        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.EAST);
        leftPanel.add(lblStatus, BorderLayout.SOUTH);

        // RIGHT: Table Panel
        String[] columns = {"ΑΦΜ", "Ονοματεπώνυμο", "Τηλέφωνο", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCustomers = new JTable(tableModel);
        tableCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableCustomers);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, scrollPane);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnSearch.addActionListener(e -> handleSearch());
        btnClear.addActionListener(e -> handleClear());
        btnBack.addActionListener(e -> goBack());

        // Table Selection Listener
        tableCustomers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableCustomers.getSelectedRow();
                if (row >= 0) {
                    fillFormFromTable(row);
                }
            }
        });
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
        loadTable();
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                    c.getAfm(),
                    c.getFullName(),
                    c.getPhoneNumber(),
                    c.getEmail()
            });
        }
    }

    private void fillFormFromTable(int row) {
        txtAfm.setText((String) tableModel.getValueAt(row, 0));
        txtFullName.setText((String) tableModel.getValueAt(row, 1));
        txtPhone.setText((String) tableModel.getValueAt(row, 2));
        txtEmail.setText((String) tableModel.getValueAt(row, 3));
    }

    private void handleAdd() {
        try {
            String afm = safe(txtAfm);
            String fullName = safe(txtFullName);
            String phone = safe(txtPhone);
            String email = safe(txtEmail);

            if (afm.isEmpty() || fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                lblStatus.setText("Συμπληρώστε όλα τα υποχρεωτικά πεδία.");
                return;
            }
            if (!afm.matches("\\d{9}")) {
                lblStatus.setText("Το ΑΦΜ πρέπει να είναι ακριβώς 9 ψηφία.");
                return;
            }

            Customer c = new Customer(afm, fullName, phone, email);
            customerService.addCustomer(c);
            lblStatus.setText("Ο πελάτης προστέθηκε.");
            loadTable();

        } catch (Exception e) {
            lblStatus.setText("Αποτυχία προσθήκης: " + e.getMessage());
        }
    }

    private void handleUpdate() {
        int row = tableCustomers.getSelectedRow();
        if (row < 0) {
            lblStatus.setText("Επιλέξτε έναν πελάτη.");
            return;
        }

        try {
            String afm = (String) tableModel.getValueAt(row, 0);
            String fullName = safe(txtFullName);
            String phone = safe(txtPhone);
            String email = safe(txtEmail);

            if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                lblStatus.setText("Συμπληρώστε όλα τα υποχρεωτικά πεδία.");
                return;
            }

            Customer newData = new Customer(afm, fullName, phone, email);
            customerService.updateCustomer(afm, newData);
            lblStatus.setText("Ο πελάτης ενημερώθηκε.");
            loadTable();

        } catch (Exception e) {
            lblStatus.setText("Αποτυχία ενημέρωσης: " + e.getMessage());
        }
    }

    private void handleDelete() {
        int row = tableCustomers.getSelectedRow();
        if (row < 0) {
            lblStatus.setText("Επιλέξτε έναν πελάτη.");
            return;
        }

        try {
            String afm = (String) tableModel.getValueAt(row, 0);
            customerService.deleteCustomer(afm);
            lblStatus.setText("Ο πελάτης διαγράφηκε.");
            loadTable();
        } catch (Exception e) {
            lblStatus.setText("Αποτυχία διαγραφής: " + e.getMessage());
        }
    }

    private void handleSearch() {
        String afm = txtAfm.getText();
        String fullName = txtFullName.getText();
        String phone = txtPhone.getText();

        var results = customerService.searchCustomers(afm, fullName, phone);

        tableModel.setRowCount(0);
        for (Customer c : results) {
            tableModel.addRow(new Object[]{
                    c.getAfm(),
                    c.getFullName(),
                    c.getPhoneNumber(),
                    c.getEmail()
            });
        }

        lblStatus.setText("Βρέθηκαν " + results.size() + " πελάτες.");
    }

    private void handleClear() {
        txtAfm.setText("");
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        loadTable();
        lblStatus.setText("Τα φίλτρα καθαρίστηκαν.");
    }

    private String safe(JTextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim();
    }

    private void goBack() {
        mainFrame.showMainMenu(employeeService, carService, customerService, rentalService, loggedEmployee);
    }
}

