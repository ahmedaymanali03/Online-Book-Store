package com.example.onlinebookstore.Controllers;

import com.example.onlinebookstore.Models.BookStoreFacade;
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
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        
        // Validation
        if (username.isEmpty() || password.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }
        
        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters");
            return;
        }
        
        try {
            facade.registerCustomer(username, password, address, phone);
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("Account created successfully! Please login.");
            alert.showAndWait();
            
            // Navigate back to login
            handleLoginLinkAction();
        } catch (Exception e) {
            errorLabel.setText("Registration failed. Username may already exist.");
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
