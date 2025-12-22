package com.example.onlinebookstore.Controllers;

import com.example.onlinebookstore.Models.BookStoreFacade;
import com.example.onlinebookstore.Models.ValidationUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField addressField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Hyperlink loginLink;
    
    private BookStoreFacade facade = new BookStoreFacade();

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    @FXML
    protected void handleRegisterButtonAction() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        
        // Validation
        if (username.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }
        
        // Validate username format
        if (!ValidationUtils.isValidUsername(username)) {
            errorLabel.setText(ValidationUtils.getUsernameErrorMessage());
            return;
        }
        
        // Validate password
        if (!ValidationUtils.isValidPassword(password)) {
            errorLabel.setText(ValidationUtils.getPasswordErrorMessage());
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }
        
        // Validate phone number (Egyptian format: 01X XXXX XXXX)
        if (!ValidationUtils.isValidPhone(phone)) {
            errorLabel.setText(ValidationUtils.getPhoneErrorMessage());
            return;
        }
        
        try {
            boolean success = facade.registerCustomer(username, password, address, phone);
            
            if (!success) {
                errorLabel.setText("Username already exists. Please choose a different one.");
                return;
            }
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully! Please login.");
            alert.showAndWait();
            
            // Navigate back to login
            handleLoginLinkAction();
        } catch (Exception e) {
            errorLabel.setText("Registration failed. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleLoginLinkAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
