module com.example.onlinebookstore {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.onlinebookstore to javafx.fxml;
    exports com.example.onlinebookstore;
}