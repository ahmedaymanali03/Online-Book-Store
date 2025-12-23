package com.example.onlinebookstore.Controllers;

import com.example.onlinebookstore.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane;


import java.util.List;


public class CustomerDashboardController {
    

    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Button ordersButton;
    
    @FXML
    private Button accountButton;

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
    private ListView<Book> bookListView;
    
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
        sortFilter.setItems(FXCollections.observableArrayList(
            "None", 
            "Title (A-Z)", 
            "Title (Z-A)", 
            "Price (Low to High)", 
            "Price (High to Low)",
            "Popularity"
        ));
        sortFilter.setValue("None");
        
        // Load categories
        List<Category> categories = facade.getAllCategories();
        ObservableList<String> categoryNames = FXCollections.observableArrayList("All Categories");
        for (Category cat : categories) {
            categoryNames.add(cat.getName());
        }
        categoryFilter.setItems(categoryNames);
        categoryFilter.setValue("All Categories");
        
        // Add listeners to automatically apply filters when changed
        categoryFilter.setOnAction(e -> handleFilterAction());
        sortFilter.setOnAction(e -> handleFilterAction());
        
        // Add dynamic search listener - updates results as user types
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearchAction();
        });
        
        // Load all books initially
        loadBooks(facade.getAllBooks());
        updateCartButton();
    }

    private void loadBooks(List<Book> books) {
        this.currentBooks = books;
        ObservableList<Book> bookItems = FXCollections.observableArrayList(books);
        bookListView.setItems(bookItems);
        
        // Set custom cell factory to display book covers
        bookListView.setCellFactory(param -> new ListCell<Book>() {
            private ImageView imageView = new ImageView();
            private Label titleLabel = new Label();
            private Label authorLabel = new Label();
            private Label priceLabel = new Label();
            private Label stockLabel = new Label();
            private HBox content = new HBox(10);
            private VBox textBox = new VBox(5);
            
            {
                imageView.setFitWidth(60);
                imageView.setFitHeight(90);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                authorLabel.setStyle("-fx-text-fill: #666666;");
                priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2ecc71;");
                stockLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
                
                textBox.getChildren().addAll(titleLabel, authorLabel, priceLabel, stockLabel);
                textBox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(textBox, Priority.ALWAYS);
                
                content.getChildren().addAll(imageView, textBox);
                content.setAlignment(Pos.CENTER_LEFT);
                content.setStyle("-fx-padding: 5;");
            }
            
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText(book.getTitle());
                    authorLabel.setText("by " + book.getAuthor());
                    priceLabel.setText(String.format("$%.2f", book.getPrice()));
                    stockLabel.setText("Stock: " + book.getStock());
                    
                    // Load book cover image from resources
                    if (book.getCoverImage() != null && !book.getCoverImage().isEmpty()) {
                        try {
                            // Load from classpath resources
                            Image image = new Image(getClass().getResourceAsStream(book.getCoverImage()));
                            imageView.setImage(image);
                        } catch (Exception e) {
                            imageView.setImage(null);
                        }
                    } else {
                        imageView.setImage(null);
                    }
                    
                    setGraphic(content);
                }
            }
        });
        
        // Add double-click listener to show book details
        bookListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    showBookDetails(selectedBook);
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
        
        // Add book cover image if available
        if (book.getCoverImage() != null && !book.getCoverImage().isEmpty()) {
            try {
                javafx.scene.image.Image coverImage = new javafx.scene.image.Image(getClass().getResourceAsStream(book.getCoverImage()));
                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(coverImage);
                imageView.setFitWidth(200);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
                
                javafx.scene.layout.HBox imageBox = new javafx.scene.layout.HBox(imageView);
                imageBox.setAlignment(javafx.geometry.Pos.CENTER);
                content.getChildren().add(imageBox);
            } catch (Exception e) {
                // If image fails to load, show placeholder
                Label noImage = new Label("📚 [Cover Image Unavailable]");
                noImage.setStyle("-fx-font-size: 16; -fx-text-fill: #999999;");
                javafx.scene.layout.HBox imageBox = new javafx.scene.layout.HBox(noImage);
                imageBox.setAlignment(javafx.geometry.Pos.CENTER);
                imageBox.setPrefHeight(100);
                content.getChildren().add(imageBox);
            }
        } else {
            // No cover image available
            Label noImage = new Label("📚 [No Cover Image]");
            noImage.setStyle("-fx-font-size: 16; -fx-text-fill: #999999;");
            javafx.scene.layout.HBox imageBox = new javafx.scene.layout.HBox(noImage);
            imageBox.setAlignment(javafx.geometry.Pos.CENTER);
            imageBox.setPrefHeight(100);
            content.getChildren().add(imageBox);
        }
        
        // Book details
        content.getChildren().addAll(
            new Label("Price: $" + book.getPrice()),
            new Label("Category: " + book.getCategory()),
            new Label("Stock: " + book.getStock()),
            new Label("Edition: " + (book.getEdition() != null ? book.getEdition() : "N/A"))
        );
        
        // Get and display average rating
        double avgRating = facade.getBookAverageRating(book.getId());
        if (avgRating > 0) {
            Label ratingLabel = new Label(String.format("Average Rating: %.1f ⭐ / 5.0", avgRating));
            ratingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #f39c12;");
            content.getChildren().add(ratingLabel);
        }
        
        // Get and display reviews
        List<Review> reviews = facade.getBookReviews(book.getId());
        if (reviews != null && !reviews.isEmpty()) {
            Label reviewsHeader = new Label("\nCustomer Reviews (" + reviews.size() + "):");
            reviewsHeader.setStyle("-fx-font-weight: bold;");
            content.getChildren().add(reviewsHeader);
            
            // Create scrollable area for reviews
            VBox reviewsBox = new VBox(8);
            for (Review review : reviews) {
                VBox reviewItem = new VBox(3);
                reviewItem.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #f9f9f9;");
                
                String stars = "⭐".repeat(review.getRating());
                Label ratingLine = new Label(stars + " (" + review.getRating() + "/5)");
                ratingLine.setStyle("-fx-font-weight: bold;");
                
                Label commentLine = new Label(review.getComment());
                commentLine.setWrapText(true);
                commentLine.setMaxWidth(400);
                
                String reviewDate = review.getReviewDate();
                if (reviewDate.length() > 10) {
                    reviewDate = reviewDate.substring(0, 10);
                }
                Label dateLine = new Label("Date: " + reviewDate);
                dateLine.setStyle("-fx-font-size: 10; -fx-text-fill: #666666;");
                
                reviewItem.getChildren().addAll(ratingLine, commentLine, dateLine);
                reviewsBox.getChildren().add(reviewItem);
            }
            
            ScrollPane reviewsScrollPane = new ScrollPane(reviewsBox);
            reviewsScrollPane.setFitToWidth(true);
            reviewsScrollPane.setPrefHeight(200);
            reviewsScrollPane.setMaxHeight(200);
            content.getChildren().add(reviewsScrollPane);
        } else {
            Label noReviews = new Label("\nNo reviews yet for this book.");
            noReviews.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
            content.getChildren().add(noReviews);
        }
        
        // Separator before add to cart section
        Separator separator = new Separator();
        content.getChildren().add(separator);
        
        // Calculate available stock (total stock minus what's already in cart)
        int alreadyInCart = 0;
        Cart cart = facade.getCustomerCart();
        if (cart != null && cart.getItems().containsKey(book.getId())) {
            alreadyInCart = cart.getItems().get(book.getId());
        }
        int availableToAdd = book.getStock() - alreadyInCart;
        
        // Quantity spinner
        Label qtyLabel = new Label("Quantity:");
        Spinner<Integer> quantitySpinner;
        Label stockInfoLabel = new Label();
        
        if (availableToAdd <= 0) {
            // No stock available to add
            quantitySpinner = new Spinner<>(0, 0, 0);
            quantitySpinner.setDisable(true);
            stockInfoLabel.setText("You already have " + alreadyInCart + " in cart (max stock: " + book.getStock() + ")");
            stockInfoLabel.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            quantitySpinner = new Spinner<>(1, availableToAdd, 1);
            if (alreadyInCart > 0) {
                stockInfoLabel.setText("Already in cart: " + alreadyInCart + " | Available to add: " + availableToAdd);
                stockInfoLabel.setStyle("-fx-text-fill: #7f8c8d;");
            }
        }
        
        content.getChildren().addAll(qtyLabel, quantitySpinner);
        if (!stockInfoLabel.getText().isEmpty()) {
            content.getChildren().add(stockInfoLabel);
        }
        
        ScrollPane mainScrollPane = new ScrollPane(content);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setPrefWidth(450);
        mainScrollPane.setPrefHeight(500);
        
        dialog.getDialogPane().setContent(mainScrollPane);
        
        // Create custom "Add to Cart" button
        ButtonType addToCartButton = new ButtonType("Add to Cart", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addToCartButton, ButtonType.CANCEL);
        
        final int finalAvailableToAdd = availableToAdd;
        dialog.showAndWait().ifPresent(response -> {
            if (response == addToCartButton) {
                // Check if user is logged in
                if (!SessionManager.getInstance().isLoggedIn()) {
                    showAlert("Login Required", "Please login to add items to your cart.", Alert.AlertType.WARNING);
                    return;
                }
                
                // Check stock availability
                if (finalAvailableToAdd <= 0) {
                    showAlert("Stock Limit", "You already have the maximum available quantity in your cart.", Alert.AlertType.WARNING);
                    return;
                }
                
                int quantity = quantitySpinner.getValue();
                if (quantity > finalAvailableToAdd) {
                    showAlert("Stock Limit", "Cannot add more than " + finalAvailableToAdd + " items. Stock limit reached.", Alert.AlertType.WARNING);
                    return;
                }
                
                facade.addBookToCart(book, quantity);
                updateCartButton();
                showAlert("Success", "Added to cart!", Alert.AlertType.INFORMATION);
            }
        });
    }

    @FXML
    protected void handleSearchAction() {
        String searchText = searchField.getText().trim();
        List<Book> books;
        
        // First, apply category filter
        String selectedCategory = categoryFilter.getValue();
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            books = facade.filterBooksByCategory(selectedCategory);
        } else {
            books = facade.getAllBooks();
        }
        
        // Then, apply search filter within the filtered books
        if (!searchText.isEmpty()) {
            String lowerSearchText = searchText.toLowerCase();
            books = books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerSearchText) ||
                               book.getAuthor().toLowerCase().contains(lowerSearchText))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Finally, apply sort using Strategy Pattern via Facade
        String sortBy = sortFilter.getValue();
        facade.sortBooks(books, sortBy);
        
        loadBooks(books);
    }

    @FXML
    protected void handleFilterAction() {
        List<Book> books;
        
        // Apply category filter first
        String selectedCategory = categoryFilter.getValue();
        if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
            books = facade.filterBooksByCategory(selectedCategory);
        } else {
            books = facade.getAllBooks();
        }
        
        // Then apply search filter within the filtered books
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            String lowerSearchText = searchText.toLowerCase();
            books = books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerSearchText) ||
                               book.getAuthor().toLowerCase().contains(lowerSearchText))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Finally apply sort using Strategy Pattern via Facade
        String sortBy = sortFilter.getValue();
        facade.sortBooks(books, sortBy);
        
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
    protected void handleAccountButtonAction() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            showAlert("Login Required", "Please login to view your account", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/AccountView.fxml"));
            Parent root = loader.load();
            AccountController controller = loader.getController();
            controller.setFacade(facade);

            Stage stage = (Stage) accountButton.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
            stage.setTitle("My Account");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open account view", Alert.AlertType.ERROR);
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
            updateCartButton();
            
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
        if (cart != null && !cart.getItems().isEmpty()) {
            int itemCount = cart.getItems().values().stream().mapToInt(Integer::intValue).sum();
            cartButton.setText("View Cart (" + itemCount + ")");
        } else {
            cartButton.setText("View Cart");
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
