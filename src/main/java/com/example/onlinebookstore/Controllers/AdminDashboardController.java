package com.example.onlinebookstore.Controllers;

import com.example.onlinebookstore.Models.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class AdminDashboardController {
    // Book Management
    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TableColumn<Book, Integer> bookIdCol;
    @FXML
    private TableColumn<Book, String> bookTitleCol;
    @FXML
    private TableColumn<Book, String> bookAuthorCol;
    @FXML
    private TableColumn<Book, Double> bookPriceCol;
    @FXML
    private TableColumn<Book, Integer> bookStockCol;
    @FXML
    private TableColumn<Book, String> bookCategoryCol;
    
    @FXML
    private Button addBookButton;
    @FXML
    private Button editBookButton;
    @FXML
    private Button deleteBookButton;
    
    // Order Management
    @FXML
    private TableView<Order> orderTableView;
    @FXML
    private TableColumn<Order, Integer> orderIdCol;
    @FXML
    private TableColumn<Order, Integer> orderCustomerCol;
    @FXML
    private TableColumn<Order, String> orderDateCol;
    @FXML
    private TableColumn<Order, String> orderStatusCol;
    @FXML
    private TableColumn<Order, Double> orderTotalCol;
    
    @FXML
    private Button confirmOrderButton;
    @FXML
    private Button cancelOrderButton;
    
    // Statistics
    @FXML
    private ListView<String> topBooksList;
    @FXML
    private PieChart salesPieChart;
    
    @FXML
    private Button logoutButton;
    
    private BookStoreFacade facade;

    public void setFacade(BookStoreFacade facade) {
        this.facade = facade;
        initialize();
    }

    @FXML
    public void initialize() {
        if (facade != null) {
            setupBookTable();
            setupOrderTable();
            loadBooks();
            loadOrders();
            loadStatistics();
        }
    }

    private void setupBookTable() {
        bookIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        bookTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        bookAuthorCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        bookPriceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        bookStockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
        bookCategoryCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
    }

    private void setupOrderTable() {
        orderIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        orderCustomerCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
        orderDateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOrderDate()));
        orderStatusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        orderTotalCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());
    }

    private void loadBooks() {
        List<Book> books = facade.getAllBooks();
        ObservableList<Book> bookData = FXCollections.observableArrayList(books);
        bookTableView.setItems(bookData);
    }

    private void loadOrders() {
        List<Order> orders = facade.getAllOrders();
        if (orders != null) {
            ObservableList<Order> orderData = FXCollections.observableArrayList(orders);
            orderTableView.setItems(orderData);
        }
    }

    private void loadStatistics() {
        // Load top selling books
        List<Book> topBooks = facade.getTopSellingBooks(10);
        if (topBooks != null) {
            ObservableList<String> topBooksData = FXCollections.observableArrayList();
            for (Book book : topBooks) {
                topBooksData.add(String.format("%s - %d sold", book.getTitle(), book.getPopularity()));
            }
            topBooksList.setItems(topBooksData);
        }
        
        // Load sales by category chart
        Map<String, Double> salesByCategory = facade.getSalesByCategory();
        if (salesByCategory != null) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> entry : salesByCategory.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            salesPieChart.setData(pieChartData);
        }
    }

    @FXML
    protected void handleAddBookAction() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField priceField = new TextField();
        TextField stockField = new TextField();
        TextField categoryField = new TextField();
        TextField editionField = new TextField();

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);
        grid.add(new Label("Edition:"), 0, 5);
        grid.add(editionField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Book book = new Book(
                        0,
                        titleField.getText(),
                        authorField.getText(),
                        Double.parseDouble(priceField.getText()),
                        Integer.parseInt(stockField.getText()),
                        categoryField.getText(),
                        0,
                        editionField.getText(),
                        null
                    );
                    return book;
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for price and stock", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(book -> {
            facade.addBook(book);
            loadBooks();
            showAlert("Success", "Book added successfully", Alert.AlertType.INFORMATION);
        });
    }

    @FXML
    protected void handleEditBookAction() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("No Selection", "Please select a book to edit", Alert.AlertType.WARNING);
            return;
        }

        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Edit book details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(selectedBook.getTitle());
        TextField authorField = new TextField(selectedBook.getAuthor());
        TextField priceField = new TextField(String.valueOf(selectedBook.getPrice()));
        TextField stockField = new TextField(String.valueOf(selectedBook.getStock()));
        TextField categoryField = new TextField(selectedBook.getCategory());
        TextField editionField = new TextField(selectedBook.getEdition());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryField, 1, 4);
        grid.add(new Label("Edition:"), 0, 5);
        grid.add(editionField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    selectedBook.setTitle(titleField.getText());
                    selectedBook.setAuthor(authorField.getText());
                    selectedBook.setPrice(Double.parseDouble(priceField.getText()));
                    selectedBook.setStock(Integer.parseInt(stockField.getText()));
                    selectedBook.setCategory(categoryField.getText());
                    selectedBook.setEdition(editionField.getText());
                    return selectedBook;
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers for price and stock", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(book -> {
            facade.updateBook(book);
            loadBooks();
            showAlert("Success", "Book updated successfully", Alert.AlertType.INFORMATION);
        });
    }

    @FXML
    protected void handleDeleteBookAction() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("No Selection", "Please select a book to delete", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Book");
        confirmAlert.setContentText("Are you sure you want to delete \"" + selectedBook.getTitle() + "\"?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                facade.deleteBook(selectedBook.getId());
                loadBooks();
                showAlert("Success", "Book deleted successfully", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    protected void handleConfirmOrderAction() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("No Selection", "Please select an order to confirm", Alert.AlertType.WARNING);
            return;
        }

        if (!"PENDING".equals(selectedOrder.getStatus())) {
            showAlert("Cannot Confirm", "Only pending orders can be confirmed", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Order");
        confirmAlert.setHeaderText("Confirm Order #" + selectedOrder.getId());
        confirmAlert.setContentText("Are you sure you want to confirm this order?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                facade.confirmOrder(selectedOrder);
                loadOrders();
                showAlert("Success", "Order confirmed successfully", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    protected void handleCancelOrderAction() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("No Selection", "Please select an order to cancel", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Order");
        confirmAlert.setHeaderText("Cancel Order #" + selectedOrder.getId());
        confirmAlert.setContentText("Are you sure you want to cancel this order?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                facade.cancelOrderAdmin(selectedOrder.getId());
                loadOrders();
                showAlert("Success", "Order cancelled successfully", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    protected void handleLogoutAction() {
        // Clear session
        SessionManager.getInstance().clearSession();
        
        facade.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
