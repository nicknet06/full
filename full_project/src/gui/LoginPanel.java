package gui;

import api.entities.Employee;
import api.services.CarService;
import api.services.CustomerService;
import api.services.EmployeeService;
import api.services.RentalService;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblStatus;
    private JButton btnLogin;

    private EmployeeService employeeService;
    private CarService carService;
    private CustomerService customerService;
    private RentalService rentalService;

    private MainFrame mainFrame;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Aυθεντικοποίηση", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Όνομα Χρήστη:"), gbc);

        txtUsername = new JTextField(22);
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Κωδικός:"), gbc);

        txtPassword = new JPasswordField(22);
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // Login Button
        btnLogin = new JButton("Σύνδεση");
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnLogin, gbc);

        // Status Label
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(Color.RED);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridy = 4;
        add(lblStatus, gbc);

        // Action Listeners
        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addActionListener(e -> handleLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    public void init(EmployeeService empService,
                     CarService carService,
                     CustomerService custService,
                     RentalService rentalService) {
        this.employeeService = empService;
        this.carService = carService;
        this.customerService = custService;
        this.rentalService = rentalService;
    }

    private void handleLogin() {
        try {
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword()).trim();

            // 1) Empty validation
            if (user.isEmpty() || pass.isEmpty()) {
                lblStatus.setText("Συμπληρώστε όλα τα πεδία");
                return;
            }

            // 2) Check credentials
            if (!employeeService.validateLogin(user, pass)) {
                lblStatus.setText("Λάθος στοιχεία σύνδεσης");
                return;
            }

            // 3) Fetch logged user object
            Employee logged = employeeService.findByUsername(user);

            // 4) Switch to Main Menu
            mainFrame.showMainMenu(employeeService, carService, customerService, rentalService, logged);

        } catch (Exception e) {
            lblStatus.setText("Σφάλμα συστήματος — προσπαθήστε ξανά");
            e.printStackTrace();
        }
    }

    public void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblStatus.setText(" ");
    }
}

