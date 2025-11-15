package com.example.onlinebookstore;

import com.example.onlinebookstore.Models.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database on startup
        DatabaseManager.getInstance();
        
        // Start with customer dashboard
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/onlinebookstore/CustomerDashboardView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setTitle("Online Book Store");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}