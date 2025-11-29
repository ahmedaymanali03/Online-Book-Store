package com.example.onlinebookstore.Controllers;

import com.example.onlinebookstore.Models.BookStoreFacade;
import com.example.onlinebookstore.Models.Customer;
import com.example.onlinebookstore.Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller responsible for displaying and updating the logged-in customer's account details.
 */
public class AccountController {

    @FXML
    private Label usernameDisplay;

    @FXML
    private Label addressDisplay;

    @FXML
    private Label phoneDisplay;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label confirmPasswordLabel;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button editButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private BookStoreFacade facade;
    private boolean isEditMode = false;

    @FXML
    private void initialize() {
        feedbackLabel.setText("");
        populateDisplayFromSession();
    }

    public void setFacade(BookStoreFacade facade) {
        this.facade = facade;
        populateDisplayFromSession();
    }

    @FXML
    private void handleEditAction() {
        isEditMode = true;
        toggleEditMode();
    }

    @FXML
    private void handleCancelAction() {
        isEditMode = false;
        toggleEditMode();
        populateDisplayFromSession();
        feedbackLabel.setText("");
        passwordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleSaveAction() {
        feedbackLabel.setStyle("-fx-text-fill: #d00000;");
        feedbackLabel.setText("");

        if (facade == null) {
            feedbackLabel.setText("Application not ready. Please return to dashboard.");
            return;
        }

        User sessionUser = SessionManager.getInstance().getCurrentUser();
        if (!(sessionUser instanceof Customer)) {
            feedbackLabel.setText("Only customers can update account details.");
            return;
        }

        Customer customer = (Customer) sessionUser;

        String username = usernameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            feedbackLabel.setText("All fields except password are required.");
            return;
        }

        boolean wantsPasswordChange = !newPassword.isEmpty() || !confirmPassword.isEmpty();
        if (wantsPasswordChange) {
            if (!newPassword.equals(confirmPassword)) {
                feedbackLabel.setText("Passwords do not match.");
                return;
            }
        } else {
            newPassword = customer.getPassword();
        }

        boolean updateResult = facade.updateCurrentCustomer(username, newPassword, address, phone);
        if (!updateResult) {
            feedbackLabel.setText("Failed to update account. Please try again.");
            return;
        }

        SessionManager.getInstance().setCurrentUser(customer);
        passwordField.clear();
        confirmPasswordField.clear();
        isEditMode = false;
        toggleEditMode();
        populateDisplayFromSession();

        feedbackLabel.setStyle("-fx-text-fill: #008000;");
        feedbackLabel.setText("Account details updated successfully!");
    }

    @FXML
    private void handleBackAction() {
        navigateToDashboard();
    }

    private void toggleEditMode() {
        // Toggle display labels vs edit fields
        usernameDisplay.setVisible(!isEditMode);
        usernameDisplay.setManaged(!isEditMode);
        usernameField.setVisible(isEditMode);
        usernameField.setManaged(isEditMode);

        addressDisplay.setVisible(!isEditMode);
        addressDisplay.setManaged(!isEditMode);
        addressField.setVisible(isEditMode);
        addressField.setManaged(isEditMode);

        phoneDisplay.setVisible(!isEditMode);
        phoneDisplay.setManaged(!isEditMode);
        phoneField.setVisible(isEditMode);
        phoneField.setManaged(isEditMode);

        // Password fields only visible in edit mode
        passwordLabel.setVisible(isEditMode);
        passwordLabel.setManaged(isEditMode);
        passwordField.setVisible(isEditMode);
        passwordField.setManaged(isEditMode);

        confirmPasswordLabel.setVisible(isEditMode);
        confirmPasswordLabel.setManaged(isEditMode);
        confirmPasswordField.setVisible(isEditMode);
        confirmPasswordField.setManaged(isEditMode);

        // Toggle buttons
        editButton.setVisible(!isEditMode);
        editButton.setManaged(!isEditMode);
        saveButton.setVisible(isEditMode);
        saveButton.setManaged(isEditMode);
        cancelButton.setVisible(isEditMode);
        cancelButton.setManaged(isEditMode);

        // Populate edit fields when entering edit mode
        if (isEditMode) {
            User sessionUser = SessionManager.getInstance().getCurrentUser();
            if (sessionUser instanceof Customer customer) {
                usernameField.setText(customer.getUsername());
                addressField.setText(customer.getAddress());
                phoneField.setText(customer.getPhone());
            }
        }
    }

    private void populateDisplayFromSession() {
        User sessionUser = SessionManager.getInstance().getCurrentUser();
        if (sessionUser instanceof Customer customer) {
            usernameDisplay.setText(customer.getUsername());
            addressDisplay.setText(customer.getAddress());
            phoneDisplay.setText(customer.getPhone());
        }
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/CustomerDashboardView.fxml"));
            Parent root = loader.load();
            CustomerDashboardController controller = loader.getController();
            controller.setFacade(facade);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle("Online Book Store");
        } catch (Exception e) {
            e.printStackTrace();
            feedbackLabel.setStyle("-fx-text-fill: #d00000;");
            feedbackLabel.setText("Unable to open dashboard. Please try again.");
        }
    }
}
