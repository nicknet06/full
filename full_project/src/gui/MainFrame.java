package gui;

import api.entities.Employee;
import api.services.CarService;
import api.services.CustomerService;
import api.services.EmployeeService;
import api.services.RentalService;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame that uses CardLayout to switch between different panels.
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;

    // Panels
    private LoginPanel loginPanel;
    private MainMenuPanel mainMenuPanel;
    private CarFormPanel carFormPanel;
    private CustomerFormPanel customerFormPanel;
    private EmployeeFormPanel employeeFormPanel;
    private RentalFormPanel rentalFormPanel;
    private ReturnRentalPanel returnRentalPanel;
    private CustomerRentalHistoryPanel historyPanel;

    // Card names
    private static final String LOGIN = "LOGIN";
    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String CAR_FORM = "CAR_FORM";
    private static final String CUSTOMER_FORM = "CUSTOMER_FORM";
    private static final String EMPLOYEE_FORM = "EMPLOYEE_FORM";
    private static final String RENTAL_FORM = "RENTAL_FORM";
    private static final String RETURN_RENTAL = "RETURN_RENTAL";
    private static final String HISTORY = "HISTORY";

    public MainFrame() {
        super("Σύστημα Ενοικίασεων Αυτοκινήτων");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Create all panels
        loginPanel = new LoginPanel(this);
        mainMenuPanel = new MainMenuPanel(this);
        carFormPanel = new CarFormPanel(this);
        customerFormPanel = new CustomerFormPanel(this);
        employeeFormPanel = new EmployeeFormPanel(this);
        rentalFormPanel = new RentalFormPanel(this);
        returnRentalPanel = new ReturnRentalPanel(this);
        historyPanel = new CustomerRentalHistoryPanel(this);

        // Add panels to card layout
        mainContainer.add(loginPanel, LOGIN);
        mainContainer.add(mainMenuPanel, MAIN_MENU);
        mainContainer.add(carFormPanel, CAR_FORM);
        mainContainer.add(customerFormPanel, CUSTOMER_FORM);
        mainContainer.add(employeeFormPanel, EMPLOYEE_FORM);
        mainContainer.add(rentalFormPanel, RENTAL_FORM);
        mainContainer.add(returnRentalPanel, RETURN_RENTAL);
        mainContainer.add(historyPanel, HISTORY);

        add(mainContainer);

        // Start with login
        cardLayout.show(mainContainer, LOGIN);

        pack();
        setLocationRelativeTo(null);
    }

    // Initialize login panel with services
    public void initLogin(EmployeeService empService, CarService carService,
                          CustomerService custService, RentalService rentalService) {
        loginPanel.init(empService, carService, custService, rentalService);
    }

    // Navigation methods
    public void showLogin(EmployeeService empService, CarService carService,
                          CustomerService custService, RentalService rentalService) {
        loginPanel.init(empService, carService, custService, rentalService);
        loginPanel.clearFields();
        cardLayout.show(mainContainer, LOGIN);
    }

    public void showMainMenu(EmployeeService empService, CarService carService,
                             CustomerService custService, RentalService rentalService,
                             Employee loggedEmployee) {
        mainMenuPanel.init(empService, carService, custService, rentalService, loggedEmployee);
        cardLayout.show(mainContainer, MAIN_MENU);
    }

    public void showCarForm(EmployeeService empService, CarService carService,
                            CustomerService custService, RentalService rentalService,
                            Employee loggedEmployee) {
        carFormPanel.init(empService, carService, custService, rentalService, loggedEmployee);
        cardLayout.show(mainContainer, CAR_FORM);
    }

    public void showCustomerForm(EmployeeService empService, CarService carService,
                                 CustomerService custService, RentalService rentalService,
                                 Employee loggedEmployee) {
        customerFormPanel.init(empService, carService, custService, rentalService, loggedEmployee);
        cardLayout.show(mainContainer, CUSTOMER_FORM);
    }

    public void showEmployeeForm(EmployeeService empService, CarService carService,
                                 CustomerService custService, RentalService rentalService,
                                 Employee loggedEmployee) {
        employeeFormPanel.init(empService, carService, custService, rentalService, loggedEmployee);
        cardLayout.show(mainContainer, EMPLOYEE_FORM);
    }

    public void showRentalForm(EmployeeService empService, CarService carService,
                               CustomerService custService, RentalService rentalService,
                               Employee loggedEmployee) {
        rentalFormPanel.init(empService, carService, custService, rentalService, loggedEmployee);
        cardLayout.show(mainContainer, RENTAL_FORM);
    }

    public void showReturnRental(EmployeeService empService, CarService carService,
                                 CustomerService custService, RentalService rentalService,
                                 Employee loggedEmployee) {
        returnRentalPanel.init(empService, carService, custService, rentalService, loggedEmployee);
        cardLayout.show(mainContainer, RETURN_RENTAL);
    }

    public void showHistory(EmployeeService empService, CarService carService,
                            CustomerService custService, RentalService rentalService,
                            Employee loggedEmployee) {
        historyPanel.init(empService, carService, custService, rentalService, loggedEmployee);
        cardLayout.show(mainContainer, HISTORY);
    }
}

