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
import java.util.stream.Collectors;

public class ReturnRentalPanel extends JPanel {

    private JTextField txtRentalId;
    private JTextField txtAfm;
    private JTextField txtPlate;
    private JTable tableActive;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    private CarService carService;
    private EmployeeService employeeService;
    private CustomerService customerService;
    private RentalService rentalService;
    private Employee loggedEmployee;

    private MainFrame mainFrame;

    public ReturnRentalPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Επιστροφή Ενοικίασης", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // TOP: Search Panel
        JPanel searchPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Αναζήτηση Ενοικίασης"));
        searchPanel.add(new JLabel("ΑΦΜ Πελάτη:"));
        txtAfm = new JTextField(15);
        searchPanel.add(txtAfm);
        JButton btnSearch = new JButton("Αναζήτηση");
        searchPanel.add(btnSearch);

        searchPanel.add(new JLabel("Πινακίδα:"));
        txtPlate = new JTextField(15);
        searchPanel.add(txtPlate);
        searchPanel.add(new JLabel("")); // Empty space

        // CENTER: Results Panel with Table
        JPanel resultsPanel = new JPanel(new BorderLayout(5, 5));
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Ενεργές Ενοικιάσεις"));

        String[] columns = {"ID Ενοικίασης", "Πινακίδα", "ΑΦΜ", "Ημ/νία Έναρξης", "Ημ/νία Λήξης"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableActive = new JTable(tableModel);
        tableActive.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableActive);
        scrollPane.setPreferredSize(new Dimension(700, 250));
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // BOTTOM: Return Panel
        JPanel returnPanel = new JPanel(new BorderLayout(10, 10));

        JPanel returnInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        returnInputPanel.setBorder(BorderFactory.createTitledBorder("Επιστροφή"));
        returnInputPanel.add(new JLabel("ID Ενοικίασης:"));
        txtRentalId = new JTextField(20);
        returnInputPanel.add(txtRentalId);
        JButton btnReturn = new JButton("Επιστροφή");
        btnReturn.setPreferredSize(new Dimension(120, 30));
        returnInputPanel.add(btnReturn);

        returnPanel.add(returnInputPanel, BorderLayout.NORTH);

        // Status and Back
        JPanel bottomPanel = new JPanel(new BorderLayout());
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        bottomPanel.add(lblStatus, BorderLayout.CENTER);

        JButton btnBack = new JButton("Πίσω");
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.add(btnBack);
        bottomPanel.add(backPanel, BorderLayout.SOUTH);

        returnPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Assemble main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        mainPanel.add(returnPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Action Listeners
        btnSearch.addActionListener(e -> handleSearch());
        btnReturn.addActionListener(e -> handleReturn());
        btnBack.addActionListener(e -> goBack());

        // Table selection listener
        tableActive.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tableActive.getSelectedRow();
                if (row >= 0) {
                    String rentalId = (String) tableModel.getValueAt(row, 0);
                    txtRentalId.setText(rentalId);
                }
            }
        });
    }

    public void init(EmployeeService empService, CarService carService,
                     CustomerService customerService, RentalService rentalService,
                     Employee loggedEmployee) {
        this.employeeService = empService;
        this.carService = carService;
        this.customerService = customerService;
        this.rentalService = rentalService;
        this.loggedEmployee = loggedEmployee;
        clearFields();
    }

    private void handleSearch() {
        String afm = txtAfm.getText().trim();
        String plate = txtPlate.getText().trim();

        List<Rental> base;

        if (!afm.isEmpty()) {
            base = rentalService.getRentalsByCustomer(afm);
        } else if (!plate.isEmpty()) {
            base = rentalService.getRentalsByCar(plate);
        } else {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Εισάγετε ΑΦΜ ή Πινακίδα.");
            tableModel.setRowCount(0);
            return;
        }

        List<Rental> active = base.stream()
                .filter(r -> r.isActive())
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Rental r : active) {
            tableModel.addRow(new Object[]{
                    r.getRentalId(),
                    r.getCar().getPlate(),
                    r.getCustomer().getAfm(),
                    r.getStartDate().toString(),
                    r.getEndDate().toString()
            });
        }

        if (active.isEmpty()) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Δεν βρέθηκαν ενεργές ενοικιάσεις.");
        } else {
            lblStatus.setForeground(new Color(0, 128, 0));
            lblStatus.setText("Ενεργές ενοικιάσεις: " + active.size());
        }
    }

    private void handleReturn() {
        String rentalId = txtRentalId.getText().trim();

        if (rentalId.isEmpty()) {
            int row = tableActive.getSelectedRow();
            if (row >= 0) {
                rentalId = (String) tableModel.getValueAt(row, 0);
            }
        }

        if (rentalId.isEmpty()) {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Επιλέξτε ή εισάγετε ένα ID Ενοικίασης.");
            return;
        }

        boolean success = rentalService.returnCar(rentalId);

        if (success) {
            lblStatus.setForeground(new Color(0, 128, 0));
            lblStatus.setText("Η ενοικίαση επιστράφηκε επιτυχώς.");
            handleSearch(); // Refresh table
            txtRentalId.setText("");
        } else {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Δεν ήταν δυνατή η επιστροφή της ενοικίασης. Ελέγξτε το ID.");
        }
    }

    private void clearFields() {
        txtAfm.setText("");
        txtPlate.setText("");
        txtRentalId.setText("");
        tableModel.setRowCount(0);
        lblStatus.setText(" ");
    }

    private void goBack() {
        mainFrame.showMainMenu(employeeService, carService, customerService, rentalService, loggedEmployee);
    }
}
