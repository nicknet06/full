package gui;

import api.entities.Car;
import api.entities.Employee;
import api.services.CarService;
import api.services.CustomerService;
import api.services.EmployeeService;
import api.services.RentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CarFormPanel extends JPanel {

    private JTable tableCars;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtPlate;
    private JTextField txtBrand;
    private JTextField txtModel;
    private JTextField txtYear;
    private JTextField txtColor;
    private JTextField txtType;
    private JComboBox<String> cmbStatus;

    private JLabel lblStatus;

    private CarService carService;
    private EmployeeService employeeService;
    private CustomerService customerService;
    private RentalService rentalService;
    private Employee loggedEmployee;

    private MainFrame mainFrame;

    public CarFormPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Διαχείριση Αυτοκινήτων", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // LEFT: Form Panel
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridy = 0;
        gbc.gridx = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        txtId = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtId, gbc);

        // Row 2
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Πινακίδα:"), gbc);
        txtPlate = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtPlate, gbc);

        // Row 3
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Μάρκα:"), gbc);
        txtBrand = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtBrand, gbc);

        // Row 4
        gbc.gridy = 3;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Μοντέλο:"), gbc);
        txtModel = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtModel, gbc);

        // Row 5
        gbc.gridy = 4;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Τύπος:"), gbc);
        txtType = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtType, gbc);

        // Row 6
        gbc.gridy = 5;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Έτος:"), gbc);
        txtYear = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtYear, gbc);

        // Row 7
        gbc.gridy = 6;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Χρώμα:"), gbc);
        txtColor = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtColor, gbc);

        // Row 8
        gbc.gridy = 7;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Κατάσταση:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"Διαθέσιμο", "Ενοικιασμένο"});
        gbc.gridx = 1;
        formPanel.add(cmbStatus, gbc);

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
        String[] columns = {"ID", "Πινακίδα", "Μάρκα", "Μοντέλο", "Τύπος", "Έτος", "Χρώμα", "Κατάσταση"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCars = new JTable(tableModel);
        tableCars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableCars);

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
        tableCars.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableCars.getSelectedRow();
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

        cmbStatus.setSelectedItem("Διαθέσιμο");
        loadTable();
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        List<Car> cars = carService.getAllCars();
        for (Car car : cars) {
            tableModel.addRow(new Object[]{
                    car.getId(),
                    car.getPlate(),
                    car.getBrand(),
                    car.getModel(),
                    car.getType(),
                    car.getYear(),
                    car.getColor(),
                    car.getStatus() ? "Διαθέσιμο" : "Ενοικιασμένο"
            });
        }
    }

    private void fillFormFromTable(int row) {
        txtId.setText((String) tableModel.getValueAt(row, 0));
        txtPlate.setText((String) tableModel.getValueAt(row, 1));
        txtBrand.setText((String) tableModel.getValueAt(row, 2));
        txtModel.setText((String) tableModel.getValueAt(row, 3));
        txtType.setText((String) tableModel.getValueAt(row, 4));
        txtYear.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        txtColor.setText((String) tableModel.getValueAt(row, 6));
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 7));
    }

    private void handleAdd() {
        try {
            String id = safe(txtId);
            String plate = safe(txtPlate);
            String brand = safe(txtBrand);
            String model = safe(txtModel);
            String type = safe(txtType);
            String color = safe(txtColor);
            String statusStr = (String) cmbStatus.getSelectedItem();

            if (id.isEmpty() || plate.isEmpty() || brand.isEmpty() || model.isEmpty() || type.isEmpty() || color.isEmpty()) {
                lblStatus.setText("Συμπληρώστε όλα τα υποχρεωτικά πεδία.");
                return;
            }
            if (statusStr == null) {
                lblStatus.setText("Επιλέξτε κατάσταση.");
                return;
            }

            boolean status = statusStr.equals("Διαθέσιμο");

            int year;
            try {
                year = Integer.parseInt(safe(txtYear));
            } catch (Exception ex) {
                lblStatus.setText("Το έτος πρέπει να είναι αριθμός.");
                return;
            }
            int currentYear = LocalDate.now().getYear();
            if (year < 1900 || year > currentYear + 1) {
                lblStatus.setText("Μη έγκυρο έτος.");
                return;
            }

            Car car = new Car(id, plate, brand, model, type, year, color, status);

            if (carService.addCar(car)) {
                lblStatus.setText("Το αυτοκίνητο προστέθηκε επιτυχώς.");
                loadTable();
                tableCars.clearSelection();
            } else {
                lblStatus.setText("Το αυτοκίνητο δεν μπόρεσε να προστεθεί (διπλό ID ή πινακίδα).");
            }

        } catch (IllegalArgumentException e) {
            lblStatus.setText(e.getMessage());
        } catch (Exception e) {
            lblStatus.setText("Μη έγκυρη είσοδος.");
        }
    }

    private void handleUpdate() {
        int row = tableCars.getSelectedRow();
        if (row < 0) {
            lblStatus.setText("Επιλέξτε πρώτα ένα αυτοκίνητο.");
            return;
        }

        try {
            String id = (String) tableModel.getValueAt(row, 0);
            String plate = safe(txtPlate);
            String brand = safe(txtBrand);
            String model = safe(txtModel);
            String type = safe(txtType);
            String color = safe(txtColor);
            String statusStr = (String) cmbStatus.getSelectedItem();

            if (plate.isEmpty() || brand.isEmpty() || model.isEmpty() || type.isEmpty() || color.isEmpty()) {
                lblStatus.setText("Συμπληρώστε όλα τα υποχρεωτικά πεδία.");
                return;
            }
            if (statusStr == null) {
                lblStatus.setText("Επιλέξτε κατάσταση.");
                return;
            }

            boolean status = statusStr.equals("Διαθέσιμο");

            int year;
            try {
                year = Integer.parseInt(safe(txtYear));
            } catch (Exception ex) {
                lblStatus.setText("Το έτος πρέπει να είναι αριθμός.");
                return;
            }

            int currentYear = LocalDate.now().getYear();
            if (year < 1900 || year > currentYear + 1) {
                lblStatus.setText("Μη έγκυρο έτος.");
                return;
            }

            Car newData = new Car(id, plate, brand, model, type, year, color, status);
            carService.updateCar(id, newData);
            lblStatus.setText("Το αυτοκίνητο ενημερώθηκε επιτυχώς.");
            loadTable();

        } catch (IllegalArgumentException e) {
            lblStatus.setText("Αποτυχία ενημέρωσης: " + e.getMessage());
        } catch (Exception e) {
            lblStatus.setText("Αποτυχία ενημέρωσης.");
        }
    }

    private void handleDelete() {
        int row = tableCars.getSelectedRow();
        if (row < 0) {
            lblStatus.setText("Επιλέξτε πρώτα ένα αυτοκίνητο.");
            return;
        }

        try {
            String id = (String) tableModel.getValueAt(row, 0);
            carService.deleteCar(id);
            lblStatus.setText("Το αυτοκίνητο διαγράφηκε.");
            loadTable();
        } catch (Exception e) {
            lblStatus.setText("Αποτυχία διαγραφής: " + e.getMessage());
        }
    }

    private void handleSearch() {
        String brand = txtBrand.getText();
        String plate = txtPlate.getText();
        String model = txtModel.getText();
        String color = txtColor.getText();
        String statusStr = (String) cmbStatus.getSelectedItem();
        Boolean status = statusStr == null ? null : statusStr.equals("Διαθέσιμο");

        var results = carService.searchCars(brand, plate, model, color, status);

        tableModel.setRowCount(0);
        for (Car car : results) {
            tableModel.addRow(new Object[]{
                    car.getId(),
                    car.getPlate(),
                    car.getBrand(),
                    car.getModel(),
                    car.getType(),
                    car.getYear(),
                    car.getColor(),
                    car.getStatus() ? "Διαθέσιμο" : "Ενοικιασμένο"
            });
        }

        lblStatus.setText("Βρέθηκαν " + results.size() + " αυτοκίνητα.");
    }

    private void handleClear() {
        txtId.setText("");
        txtPlate.setText("");
        txtBrand.setText("");
        txtModel.setText("");
        txtType.setText("");
        txtYear.setText("");
        txtColor.setText("");
        cmbStatus.setSelectedItem(null);
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
