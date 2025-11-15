package com.example.onlinebookstore.Controllers;

import com.example.onlinebookstore.Models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Hyperlink registerLink;
    
    private BookStoreFacade facade = new BookStoreFacade();

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    @FXML
    protected void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }
        
        boolean loginSuccess = facade.login(username, password);
        
        if (loginSuccess) {
            User currentUser = facade.getCurrentUser();
            
            // Store user in session
            SessionManager.getInstance().setCurrentUser(currentUser);
            
            try {
                if (currentUser instanceof Admin) {
                    // Navigate to Admin Dashboard
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/AdminDashboardView.fxml"));
                    Parent root = loader.load();
                    AdminDashboardController controller = loader.getController();
                    controller.setFacade(facade);
                    
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Admin Dashboard");
                } else if (currentUser instanceof Customer) {
                    // Navigate to Customer Dashboard
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/CustomerDashboardView.fxml"));
                    Parent root = loader.load();
                    CustomerDashboardController controller = loader.getController();
                    controller.setFacade(facade);
                    
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(new Scene(root, 1000, 700));
                    stage.setTitle("Online Book Store");
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error loading dashboard");
            }
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    @FXML
    protected void handleRegisterLinkAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/RegisterView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
