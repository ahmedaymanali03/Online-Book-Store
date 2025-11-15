package com.example.onlinebookstore.Controllers;

import com.example.onlinebookstore.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class CustomerDashboardController {
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Button ordersButton;
    
    @FXML
    private Button cartButton;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> categoryFilter;
    
    @FXML
    private ComboBox<String> sortFilter;
    
    @FXML
    private ListView<String> bookListView;
    
    private BookStoreFacade facade;
    private List<Book> currentBooks;

    public void setFacade(BookStoreFacade facade) {
        this.facade = facade;
        initialize();
    }

    @FXML
    public void initialize() {
        // Initialize facade if not set
        if (facade == null) {
            facade = new BookStoreFacade();
        }
        
        // Update login/logout button text
        updateLoginLogoutButton();
        
        User currentUser = facade.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
        } else {
            welcomeLabel.setText("Welcome, Guest");
        }
        
        // Initialize filters
        sortFilter.setItems(FXCollections.observableArrayList("Price (Low to High)", "Popularity"));
        
        // Load categories
        List<Category> categories = facade.getAllCategories();
        ObservableList<String> categoryNames = FXCollections.observableArrayList("All Categories");
        for (Category cat : categories) {
            categoryNames.add(cat.getName());
        }
        categoryFilter.setItems(categoryNames);
        
        // Load all books initially
        loadBooks(facade.getAllBooks());
        updateCartButton();
    }

    private void loadBooks(List<Book> books) {
        this.currentBooks = books;
        ObservableList<String> bookItems = FXCollections.observableArrayList();
        
        for (Book book : books) {
            String bookInfo = String.format("%s by %s - $%.2f (Stock: %d)", 
                book.getTitle(), book.getAuthor(), book.getPrice(), book.getStock());
            bookItems.add(bookInfo);
        }
        
        bookListView.setItems(bookItems);
        
        // Add context menu for adding to cart
        bookListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int selectedIndex = bookListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    showBookDetails(currentBooks.get(selectedIndex));
                }
            }
        });
    }

    private void showBookDetails(Book book) {
        // Create dialog for book details and add to cart
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(book.getTitle());
        dialog.setHeaderText(String.format("%s by %s", book.getTitle(), book.getAuthor()));
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Price: $" + book.getPrice()),
            new Label("Category: " + book.getCategory()),
            new Label("Stock: " + book.getStock()),
            new Label("Edition: " + (book.getEdition() != null ? book.getEdition() : "N/A"))
        );
        
        // Quantity spinner
        Label qtyLabel = new Label("Quantity:");
        Spinner<Integer> quantitySpinner = new Spinner<>(1, book.getStock(), 1);
        content.getChildren().addAll(qtyLabel, quantitySpinner);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                int quantity = quantitySpinner.getValue();
                facade.addBookToCart(book, quantity);
                updateCartButton();
                showAlert("Success", "Added to cart!", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    protected void handleSearchAction() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            // Search by title or author
            List<Book> titleResults = facade.searchBooksByTitle(searchText);
            List<Book> authorResults = facade.searchBooksByAuthor(searchText);
            
            // Combine results
            titleResults.addAll(authorResults);
            loadBooks(titleResults);
        } else {
            loadBooks(facade.getAllBooks());
        }
    }

    @FXML
    protected void handleFilterAction() {
        List<Book> books;
        
        // Apply category filter
        String selectedCategory = categoryFilter.getValue();
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            books = facade.filterBooksByCategory(selectedCategory);
        } else {
            books = facade.getAllBooks();
        }
        
        // Apply sort
        String sortBy = sortFilter.getValue();
        if ("Price (Low to High)".equals(sortBy)) {
            books = facade.getBooksSortedByPrice();
        } else if ("Popularity".equals(sortBy)) {
            books = facade.getBooksSortedByPopularity();
        }
        
        loadBooks(books);
    }

    @FXML
    protected void handleCartButtonAction() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            showAlert("Login Required", "Please login to view your cart", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/CartView.fxml"));
            Parent root = loader.load();
            CartController controller = loader.getController();
            controller.setFacade(facade);
            
            Stage stage = (Stage) cartButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Shopping Cart");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleOrdersButtonAction() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            showAlert("Login Required", "Please login to view your orders", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/OrderHistoryView.fxml"));
            Parent root = loader.load();
            OrderHistoryController controller = loader.getController();
            controller.setFacade(facade);
            
            Stage stage = (Stage) ordersButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Order History");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleLogoutAction() {
        if (SessionManager.getInstance().isLoggedIn()) {
            // Logout
            SessionManager.getInstance().clearSession();
            facade.logout();
            
            // Update UI for guest mode
            welcomeLabel.setText("Welcome, Guest");
            updateLoginLogoutButton();
            
            // Reload dashboard to show guest view
            loadBooks(facade.getAllBooks());
        } else {
            // Navigate to login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/LoginView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setScene(new Scene(root, 800, 600));
                stage.setTitle("Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateLoginLogoutButton() {
        if (SessionManager.getInstance().isLoggedIn()) {
            logoutButton.setText("Logout");
        } else {
            logoutButton.setText("Login");
        }
    }

    private void updateCartButton() {
        Cart cart = facade.getCustomerCart();
        if (cart != null) {
            int itemCount = cart.getItems().values().stream().mapToInt(Integer::intValue).sum();
            cartButton.setText("View Cart (" + itemCount + ")");
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
