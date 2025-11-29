package com.example.onlinebookstore.Controllers;

import com.example.onlinebookstore.Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Separator;

import java.util.Map;

public class CartController {
    @FXML
    private ListView<String> cartListView;
    
    @FXML
    private Label totalLabel;
    
    @FXML
    private Label cartInstructionLabel;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button placeOrderButton;
    
    @FXML
    private Button editQuantityButton;
    
    @FXML
    private Button removeItemButton;
    
    @FXML
    private Button clearCartButton;
    
    private BookStoreFacade facade;
    private BookDAO bookDAO = new BookDAO();
    private Map<Integer, Book> bookCache = new java.util.HashMap<>();

    @FXML
    public void initialize() {
        // Add selection listener to enable/disable buttons
        cartListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean itemSelected = newVal != null;
            editQuantityButton.setDisable(!itemSelected);
            removeItemButton.setDisable(!itemSelected);
        });
    }
    
    public void setFacade(BookStoreFacade facade) {
        this.facade = facade;
        loadCart();
    }

    private void loadCart() {
        Cart cart = facade.getCustomerCart();
        if (cart == null) return;
        
        bookCache.clear();
        ObservableList<String> cartItems = FXCollections.observableArrayList();
        double total = 0.0;
        
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookDAO.getBookByID(entry.getKey());
            if (book != null) {
                bookCache.put(entry.getKey(), book);
                int quantity = entry.getValue();
                double itemTotal = book.getPrice() * quantity;
                total += itemTotal;
                
                String itemInfo = String.format("%s x%d - $%.2f each = $%.2f", 
                    book.getTitle(), quantity, book.getPrice(), itemTotal);
                cartItems.add(itemInfo);
            }
        }
        
        cartListView.setItems(cartItems);
        totalLabel.setText(String.format("Total: $%.2f", total));
        
        if (cart.getItems().isEmpty()) {
            cartInstructionLabel.setText("Your cart is empty. Add some books!");
            placeOrderButton.setDisable(true);
        } else {
            cartInstructionLabel.setText("Select an item to edit or remove");
            placeOrderButton.setDisable(false);
        }
    }

    @FXML
    protected void handleEditQuantityAction() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) return;
        
        Cart cart = facade.getCustomerCart();
        Integer bookId = (Integer) cart.getItems().keySet().toArray()[selectedIndex];
        Book book = bookCache.get(bookId);
        int currentQty = cart.getItems().get(bookId);
        
        if (book != null) {
            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Edit Quantity");
            dialog.setHeaderText(book.getTitle());
            
            Label label = new Label("Quantity (1-" + book.getStock() + "):");
            Spinner<Integer> spinner = new Spinner<>(1, book.getStock(), currentQty);
            spinner.setEditable(true);
            
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10, label, spinner);
            content.setPadding(new javafx.geometry.Insets(10));
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return spinner.getValue();
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(newQty -> {
                facade.updateCartQuantity(book, newQty);
                loadCart();
                showAlert("Updated", "Quantity updated successfully", Alert.AlertType.INFORMATION);
            });
        }
    }
    
    @FXML
    protected void handleRemoveItemAction() {
        int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) return;
        
        Cart cart = facade.getCustomerCart();
        Integer bookId = (Integer) cart.getItems().keySet().toArray()[selectedIndex];
        Book book = bookCache.get(bookId);
        
        if (book != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Remove Item");
            confirmAlert.setHeaderText("Remove from cart?");
            confirmAlert.setContentText("Remove \"" + book.getTitle() + "\" from your cart?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    facade.removeBookFromCart(book);
                    loadCart();
                    showAlert("Removed", "Item removed from cart", Alert.AlertType.INFORMATION);
                }
            });
        }
    }
    
    @FXML
    protected void handleClearCartAction() {
        Cart cart = facade.getCustomerCart();
        if (cart == null || cart.getItems().isEmpty()) {
            showAlert("Empty Cart", "Your cart is already empty", Alert.AlertType.INFORMATION);
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Cart");
        confirmAlert.setHeaderText("Clear entire cart?");
        confirmAlert.setContentText("Remove all items from your cart?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                facade.clearCart();
                loadCart();
                showAlert("Cleared", "Cart cleared successfully", Alert.AlertType.INFORMATION);
            }
        });
    }
    
    @FXML
    protected void handlePlaceOrderAction() {
        Cart cart = facade.getCustomerCart();
        if (cart == null || cart.getItems().isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty!", Alert.AlertType.WARNING);
            return;
        }
        
        // Show checkout confirmation dialog
        Dialog<ButtonType> checkoutDialog = new Dialog<>();
        checkoutDialog.setTitle("Checkout - Confirm Order");
        checkoutDialog.setHeaderText("Order Summary");
        
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.setPadding(new javafx.geometry.Insets(10));
        
        // Display order items
        Label itemsLabel = new Label("Items:");
        itemsLabel.setStyle("-fx-font-weight: bold;");
        content.getChildren().add(itemsLabel);
        
        double totalAmount = 0.0;
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookCache.get(entry.getKey());
            if (book != null) {
                int quantity = entry.getValue();
                double itemTotal = book.getPrice() * quantity;
                totalAmount += itemTotal;
                
                Label itemLabel = new Label(String.format("  %s x%d - $%.2f", 
                    book.getTitle(), quantity, itemTotal));
                content.getChildren().add(itemLabel);
            }
        }
        
        Separator separator = new Separator();
        content.getChildren().add(separator);
        
        final double finalTotal = totalAmount;
        Label totalLabel = new Label(String.format("Total Amount: $%.2f", finalTotal));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        content.getChildren().add(totalLabel);
        
        content.getChildren().add(new Label(""));
        Label paymentLabel = new Label("Payment Method: Cash on Delivery");
        paymentLabel.setStyle("-fx-text-fill: #666666;");
        content.getChildren().add(paymentLabel);
        
        Label statusLabel = new Label("Status: Order will be pending until admin confirms");
        statusLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
        content.getChildren().add(statusLabel);
        
        checkoutDialog.getDialogPane().setContent(content);
        checkoutDialog.getDialogPane().getButtonTypes().addAll(
            new ButtonType("Place Order", ButtonBar.ButtonData.OK_DONE),
            ButtonType.CANCEL
        );
        
        checkoutDialog.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    facade.placeOrder();
                    
                    // Show order confirmation
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Order Confirmation");
                    successAlert.setHeaderText("Order Placed Successfully!");
                    successAlert.setContentText(
                        "Your order has been placed successfully.\n\n" +
                        "Order Total: $" + String.format("%.2f", finalTotal) + "\n" +
                        "Status: PENDING\n\n" +
                        "You can view and manage your orders in 'My Orders'.\n" +
                        "You can cancel this order before admin confirmation."
                    );
                    successAlert.showAndWait();
                    
                    handleBackAction();
                } catch (Exception e) {
                    showAlert("Error", "Failed to place order: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    protected void handleBackAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/onlinebookstore/CustomerDashboardView.fxml"));
            Parent root = loader.load();
            CustomerDashboardController controller = loader.getController();
            controller.setFacade(facade);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Customer Dashboard");
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
