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
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.onlinebookstore.Models.ValidationUtils;

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
    private Button updateStatusButton;
    
    // Statistics
    @FXML
    private ListView<String> topBooksList;
    @FXML
    private ListView<String> booksPerCategoryList;
    @FXML
    private ListView<String> ordersByStatusList;
    @FXML
    private PieChart salesPieChart;
    @FXML
    private PieChart inventoryPieChart;
    @FXML
    private PieChart ordersPieChart;
    @FXML
    private Button refreshStatsButton;
    @FXML
    private Tab statisticsTab;
    @FXML
    private Label totalBooksSoldLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private Label mostPopularCategoryLabel;
    
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
            
            // Auto-refresh statistics when Statistics tab is selected
            if (statisticsTab != null) {
                statisticsTab.setOnSelectionChanged(event -> {
                    if (statisticsTab.isSelected()) {
                        loadStatistics();
                    }
                });
            }
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
        orderStatusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toUpperCase()));
        orderTotalCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(Math.round(cellData.getValue().getTotalPrice() * 100.0) / 100.0).asObject());
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
        // Load summary statistics
        if (totalBooksSoldLabel != null) {
            totalBooksSoldLabel.setText(String.valueOf(facade.getTotalBooksSold()));
        }
        if (totalRevenueLabel != null) {
            totalRevenueLabel.setText(String.format("$%.2f", facade.getTotalRevenue()));
        }
        if (totalOrdersLabel != null) {
            totalOrdersLabel.setText(String.valueOf(facade.getTotalOrders()));
        }
        if (mostPopularCategoryLabel != null) {
            String popularCat = facade.getMostPopularCategory();
            mostPopularCategoryLabel.setText(popularCat != null ? popularCat : "N/A");
        }
        
        // Load top selling books
        List<Book> topBooks = facade.getTopSellingBooks(10);
        if (topBooks != null && topBooksList != null) {
            ObservableList<String> topBooksData = FXCollections.observableArrayList();
            for (Book book : topBooks) {
                topBooksData.add(String.format("%s - %d sold", book.getTitle(), book.getPopularity()));
            }
            topBooksList.setItems(topBooksData);
        }
        
        // Load books per category (list and pie chart)
        Map<String, Integer> booksPerCategory = facade.getBookCountByCategory();
        if (booksPerCategory != null) {
            if (booksPerCategoryList != null) {
                ObservableList<String> categoryData = FXCollections.observableArrayList();
                for (Map.Entry<String, Integer> entry : booksPerCategory.entrySet()) {
                    categoryData.add(String.format("%s: %d books", entry.getKey(), entry.getValue()));
                }
                booksPerCategoryList.setItems(categoryData);
            }
            if (inventoryPieChart != null) {
                ObservableList<PieChart.Data> inventoryData = FXCollections.observableArrayList();
                for (Map.Entry<String, Integer> entry : booksPerCategory.entrySet()) {
                    inventoryData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
                }
                inventoryPieChart.setData(inventoryData);
            }
        }
        
        // Load sales by category chart
        Map<String, Double> salesByCategory = facade.getSalesByCategory();
        if (salesByCategory != null && salesPieChart != null) {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Double> entry : salesByCategory.entrySet()) {
                pieChartData.add(new PieChart.Data(String.format("%s ($%.0f)", entry.getKey(), entry.getValue()), entry.getValue()));
            }
            salesPieChart.setData(pieChartData);
        }
        
        // Load orders by status (list and pie chart)
        Map<String, Integer> ordersByStatus = facade.getOrdersByStatus();
        if (ordersByStatus != null) {
            if (ordersByStatusList != null) {
                ObservableList<String> statusData = FXCollections.observableArrayList();
                for (Map.Entry<String, Integer> entry : ordersByStatus.entrySet()) {
                    statusData.add(String.format("%s: %d orders", entry.getKey(), entry.getValue()));
                }
                ordersByStatusList.setItems(statusData);
            }
            if (ordersPieChart != null) {
                ObservableList<PieChart.Data> ordersData = FXCollections.observableArrayList();
                for (Map.Entry<String, Integer> entry : ordersByStatus.entrySet()) {
                    ordersData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
                }
                ordersPieChart.setData(ordersData);
            }
        }
    }

    @FXML
    protected void handleRefreshStatsAction() {
        loadStatistics();
        loadBooks();
        loadOrders();
    }

    @FXML
    protected void handleRecalcPopularityAction() {
        // Confirm before recalculating
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Sync Popularity");
        confirm.setHeaderText("Recalculate Book Popularity?");
        confirm.setContentText("This will sync all book popularity values with actual sales data from confirmed orders. Continue?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                facade.recalculateAllPopularity();
                loadStatistics();
                loadBooks();
                showAlert("Success", "Book popularity has been synced with actual sales data!", Alert.AlertType.INFORMATION);
            }
        });
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
        
        // Use ComboBox for category selection from database
        ComboBox<Category> categoryComboBox = new ComboBox<>();
        List<Category> categories = facade.getAllCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        // Display category name in ComboBox
        categoryComboBox.setCellFactory(lv -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        categoryComboBox.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        
        TextField editionField = new TextField();
        
        // Cover Image Section
        TextField imageUrlField = new TextField();
        imageUrlField.setPromptText("Enter image URL or choose file");
        Button uploadButton = new Button("Upload File");
        Label imageStatusLabel = new Label();
        final String[] selectedImagePath = {null};
        
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Book Cover Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                try {
                    String imagePath = saveUploadedImage(selectedFile);
                    selectedImagePath[0] = imagePath;
                    imageStatusLabel.setText("✓ Image uploaded");
                    imageStatusLabel.setStyle("-fx-text-fill: green;");
                    imageUrlField.clear();
                } catch (IOException ex) {
                    imageStatusLabel.setText("✗ Upload failed");
                    imageStatusLabel.setStyle("-fx-text-fill: red;");
                }
            }
        });
        
        HBox imageBox = new HBox(10);
        imageBox.getChildren().addAll(uploadButton, imageStatusLabel);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryComboBox, 1, 4);
        grid.add(new Label("Edition:"), 0, 5);
        grid.add(editionField, 1, 5);
        grid.add(new Label("Cover Image URL:"), 0, 6);
        grid.add(imageUrlField, 1, 6);
        grid.add(new Label("Or Upload:"), 0, 7);
        grid.add(imageBox, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                Category selectedCategory = categoryComboBox.getValue();
                if (selectedCategory == null) {
                    showAlert("Invalid Input", "Please select a category", Alert.AlertType.ERROR);
                    return null;
                }
                
                // Validate required text fields
                if (titleField.getText().trim().isEmpty() || authorField.getText().trim().isEmpty()) {
                    showAlert("Invalid Input", "Title and Author are required", Alert.AlertType.ERROR);
                    return null;
                }
                
                // Validate price
                if (!ValidationUtils.isValidPrice(priceField.getText())) {
                    showAlert("Invalid Input", ValidationUtils.getPriceErrorMessage(), Alert.AlertType.ERROR);
                    return null;
                }
                
                // Validate stock
                if (!ValidationUtils.isValidStock(stockField.getText())) {
                    showAlert("Invalid Input", ValidationUtils.getStockErrorMessage(), Alert.AlertType.ERROR);
                    return null;
                }
                
                // Determine cover image path
                String coverImagePath = null;
                if (selectedImagePath[0] != null) {
                    // Use uploaded file
                    coverImagePath = selectedImagePath[0];
                } else if (!imageUrlField.getText().trim().isEmpty()) {
                    // Download from URL
                    try {
                        coverImagePath = downloadImageFromUrl(imageUrlField.getText().trim());
                    } catch (IOException ex) {
                        showAlert("Download Failed", "Could not download image from URL: " + ex.getMessage(), Alert.AlertType.WARNING);
                    }
                }
                
                Book book = new Book(
                    0,
                    titleField.getText().trim(),
                    authorField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    Integer.parseInt(stockField.getText().trim()),
                    selectedCategory,
                    0,
                    editionField.getText().trim(),
                    coverImagePath
                );
                return book;
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
        
        // Use ComboBox for category selection from database
        ComboBox<Category> categoryComboBox = new ComboBox<>();
        List<Category> categories = facade.getAllCategories();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        // Set the current category as selected
        for (Category cat : categories) {
            if (cat.getName().equals(selectedBook.getCategory())) {
                categoryComboBox.setValue(cat);
                break;
            }
        }
        // Display category name in ComboBox
        categoryComboBox.setCellFactory(lv -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        categoryComboBox.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
        
        TextField editionField = new TextField(selectedBook.getEdition());
        
        // Cover Image Section
        TextField imageUrlField = new TextField(selectedBook.getCoverImage() != null ? selectedBook.getCoverImage() : "");
        imageUrlField.setPromptText("Enter image URL or choose file");
        Button uploadButton = new Button("Upload File");
        Label imageStatusLabel = new Label();
        final String[] selectedImagePath = {selectedBook.getCoverImage()}; // Keep existing image by default
        
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Book Cover Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                try {
                    String imagePath = saveUploadedImage(selectedFile);
                    selectedImagePath[0] = imagePath;
                    imageStatusLabel.setText("\u2713 Image uploaded");
                    imageStatusLabel.setStyle("-fx-text-fill: green;");
                    imageUrlField.clear();
                } catch (IOException ex) {
                    imageStatusLabel.setText("\u2717 Upload failed");
                    imageStatusLabel.setStyle("-fx-text-fill: red;");
                }
            }
        });
        
        HBox imageBox = new HBox(10);
        imageBox.getChildren().addAll(uploadButton, imageStatusLabel);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categoryComboBox, 1, 4);
        grid.add(new Label("Edition:"), 0, 5);
        grid.add(editionField, 1, 5);
        grid.add(new Label("Cover Image URL:"), 0, 6);
        grid.add(imageUrlField, 1, 6);
        grid.add(new Label("Or Upload:"), 0, 7);
        grid.add(imageBox, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Validate required text fields
                if (titleField.getText().trim().isEmpty() || authorField.getText().trim().isEmpty()) {
                    showAlert("Invalid Input", "Title and Author are required", Alert.AlertType.ERROR);
                    return null;
                }
                
                // Validate price
                if (!ValidationUtils.isValidPrice(priceField.getText())) {
                    showAlert("Invalid Input", ValidationUtils.getPriceErrorMessage(), Alert.AlertType.ERROR);
                    return null;
                }
                
                // Validate stock
                if (!ValidationUtils.isValidStock(stockField.getText())) {
                    showAlert("Invalid Input", ValidationUtils.getStockErrorMessage(), Alert.AlertType.ERROR);
                    return null;
                }
                
                selectedBook.setTitle(titleField.getText().trim());
                selectedBook.setAuthor(authorField.getText().trim());
                selectedBook.setPrice(Double.parseDouble(priceField.getText().trim()));
                selectedBook.setStock(Integer.parseInt(stockField.getText().trim()));
                selectedBook.setCategory(categoryComboBox.getValue());
                selectedBook.setEdition(editionField.getText().trim());
                
                // Handle cover image update
                if (selectedImagePath[0] != null && !selectedImagePath[0].equals(selectedBook.getCoverImage())) {
                    // New image was uploaded or kept the same
                    selectedBook.setCoverImage(selectedImagePath[0]);
                } else if (!imageUrlField.getText().trim().isEmpty() && !imageUrlField.getText().equals(selectedBook.getCoverImage())) {
                    // Download from new URL
                    try {
                        String coverImagePath = downloadImageFromUrl(imageUrlField.getText().trim());
                        selectedBook.setCoverImage(coverImagePath);
                    } catch (IOException ex) {
                        showAlert("Download Failed", "Could not download image from URL: " + ex.getMessage(), Alert.AlertType.WARNING);
                    }
                }
                
                return selectedBook;
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
    protected void handleUpdateStatusAction() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("No Selection", "Please select an order to update", Alert.AlertType.WARNING);
            return;
        }

        List<String> statuses = List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELED");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(selectedOrder.getStatus(), statuses);
        dialog.setTitle("Update Order Status");
        dialog.setHeaderText("Update Status for Order #" + selectedOrder.getId());
        dialog.setContentText("Choose new status:");

        dialog.showAndWait().ifPresent(newStatus -> {
            if (!newStatus.equals(selectedOrder.getStatus())) {
                String oldStatus = selectedOrder.getStatus();
                boolean wasActive = !oldStatus.equals("PENDING") && !oldStatus.equals("CANCELED");
                boolean isNowActive = !newStatus.equals("PENDING") && !newStatus.equals("CANCELED");
                
                if (!wasActive && isNowActive) {
                    // Activating order (PENDING -> CONFIRMED/SHIPPED/DELIVERED): Deduct stock
                    boolean success = facade.confirmOrder(selectedOrder);
                    if (success) {
                        // If the new status is not CONFIRMED (e.g., SHIPPED, DELIVERED), update it
                        if (!newStatus.equals("CONFIRMED")) {
                            facade.updateOrderStatus(selectedOrder.getId(), newStatus);
                        }
                        loadOrders();
                        loadBooks();
                        loadStatistics();
                        showAlert("Success", "Order processed! Stock updated.", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Could not process order. Check stock availability.", Alert.AlertType.ERROR);
                    }
                } else if (wasActive && !isNowActive) {
                    // Reverting order (CONFIRMED/SHIPPED/DELIVERED -> PENDING/CANCELED): Restore stock
                    facade.revertOrder(selectedOrder, newStatus);
                    loadOrders();
                    loadBooks();
                    loadStatistics();
                    showAlert("Success", "Order reverted to " + newStatus + ". Stock restored.", Alert.AlertType.INFORMATION);
                } else {
                    // Other transitions (between active statuses, or between inactive statuses)
                    facade.updateOrderStatus(selectedOrder.getId(), newStatus);
                    loadOrders();
                    showAlert("Success", "Order status updated to " + newStatus, Alert.AlertType.INFORMATION);
                }
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
    
    /**
     * Save an uploaded image file to the assets/covers directory
     */
    private String saveUploadedImage(File sourceFile) throws IOException {
        // Create covers directory if it doesn't exist
        String coversDir = "src/main/resources/assets/covers/";
        File coversDirFile = new File(coversDir);
        if (!coversDirFile.exists()) {
            coversDirFile.mkdirs();
        }
        
        // Generate unique filename
        String extension = getFileExtension(sourceFile.getName());
        String filename = UUID.randomUUID().toString() + extension;
        String destPath = coversDir + filename;
        
        // Copy file
        Files.copy(sourceFile.toPath(), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
        
        // Return resource path
        return "/assets/covers/" + filename;
    }
    
    /**
     * Download an image from URL and save it to assets/covers directory
     */
    private String downloadImageFromUrl(String urlString) throws IOException {
        // Create covers directory if it doesn't exist
        String coversDir = "src/main/resources/assets/covers/";
        File coversDirFile = new File(coversDir);
        if (!coversDirFile.exists()) {
            coversDirFile.mkdirs();
        }
        
        // Generate unique filename
        String extension = getExtensionFromUrl(urlString);
        String filename = UUID.randomUUID().toString() + extension;
        String destPath = coversDir + filename;
        
        // Download file
        URL url = new URL(urlString);
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Return resource path
        return "/assets/covers/" + filename;
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot);
        }
        return ".jpg"; // Default extension
    }
    
    /**
     * Get file extension from URL
     */
    private String getExtensionFromUrl(String url) {
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains(".png")) return ".png";
        if (lowerUrl.contains(".gif")) return ".gif";
        if (lowerUrl.contains(".jpeg")) return ".jpeg";
        return ".jpg"; // Default extension
    }
}
