package gui;

import api.entities.Employee;
import api.services.CarService;
import api.services.CustomerService;
import api.services.EmployeeService;
import api.services.RentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeFormPanel extends JPanel {

    private JTable tableEmployees;
    private DefaultTableModel tableModel;

    private JTextField txtFullName;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;

    private JLabel lblStatus;

    private EmployeeService employeeService;
    private CarService carService;
    private CustomerService customerService;
    private RentalService rentalService;
    private Employee loggedEmployee;

    private MainFrame mainFrame;

    public EmployeeFormPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Διαχείριση Υπαλλήλων", SwingConstants.CENTER);
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
        formPanel.add(new JLabel("Ονοματεπώνυμο:"), gbc);
        txtFullName = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtFullName, gbc);

        // Row 2
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Όνομα Χρήστη:"), gbc);
        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);

        // Row 3
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        // Row 4
        gbc.gridy = 3;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Κωδικός:"), gbc);
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 10));
        JButton btnAdd = new JButton("Προσθήκη");
        JButton btnDelete = new JButton("Διαγραφή");
        JButton btnClear = new JButton("Καθαρισμός");
        JButton btnBack = new JButton("Πίσω");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
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
        String[] columns = {"Ονοματεπώνυμο", "Όνομα Χρήστη", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableEmployees = new JTable(tableModel);
        tableEmployees.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableEmployees);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, scrollPane);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        btnAdd.addActionListener(e -> handleAdd());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> handleClear());
        btnBack.addActionListener(e -> goBack());

        // Table Selection Listener
        tableEmployees.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableEmployees.getSelectedRow();
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
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee e : employees) {
            tableModel.addRow(new Object[]{
                    e.getFullName(),
                    e.getUsername(),
                    e.getEmail()
            });
        }
    }

    private void fillFormFromTable(int row) {
        txtFullName.setText((String) tableModel.getValueAt(row, 0));
        txtUsername.setText((String) tableModel.getValueAt(row, 1));
        txtEmail.setText((String) tableModel.getValueAt(row, 2));
        // Get password from service
        String username = (String) tableModel.getValueAt(row, 1);
        Employee emp = employeeService.findByUsername(username);
        if (emp != null) {
            txtPassword.setText(emp.getPassword());
        }
    }

    private void handleAdd() {
        try {
            Employee e = new Employee(
                    txtFullName.getText(),
                    txtUsername.getText(),
                    txtEmail.getText(),
                    new String(txtPassword.getPassword())
            );

            employeeService.addEmployee(e);
            lblStatus.setText("Ο υπάλληλος προστέθηκε.");
            handleClear();
            loadTable();

        } catch (Exception ex) {
            lblStatus.setText("Αποτυχία προσθήκης: " + ex.getMessage());
        }
    }

    private void handleDelete() {
        int row = tableEmployees.getSelectedRow();
        if (row < 0) {
            lblStatus.setText("Επιλέξτε έναν υπάλληλο.");
            return;
        }

        String username = (String) tableModel.getValueAt(row, 1);
        Employee selected = employeeService.findByUsername(username);

        if (selected == null) {
            lblStatus.setText("Ο υπάλληλος δεν βρέθηκε.");
            return;
        }

        // Don't allow deleting yourself
        if (loggedEmployee != null &&
                selected.getUsername().equalsIgnoreCase(loggedEmployee.getUsername())) {
            lblStatus.setText("Δεν μπορείτε να διαγράψετε τον συνδεδεμένο χρήστη.");
            return;
        }

        try {
            employeeService.deleteEmployee(selected);
            lblStatus.setText("Ο υπάλληλος διαγράφηκε.");
            handleClear();
            loadTable();

        } catch (Exception ex) {
            lblStatus.setText("Αποτυχία διαγραφής: " + ex.getMessage());
        }
    }

    private void handleClear() {
        txtFullName.setText("");
        txtUsername.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        tableEmployees.clearSelection();
        lblStatus.setText("");
    }

    private void goBack() {
        mainFrame.showMainMenu(employeeService, carService, customerService, rentalService, loggedEmployee);
    }
}

