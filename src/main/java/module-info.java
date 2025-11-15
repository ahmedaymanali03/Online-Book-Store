module com.example.onlinebookstore {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;


    opens com.example.onlinebookstore to javafx.fxml;
    exports com.example.onlinebookstore;
    exports com.example.onlinebookstore.Models;
    opens com.example.onlinebookstore.Models to javafx.fxml;
    exports com.example.onlinebookstore.Controllers;
    opens com.example.onlinebookstore.Controllers to javafx.fxml;
}