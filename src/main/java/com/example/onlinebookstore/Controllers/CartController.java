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

import java.util.Map;

public class CartController {
    @FXML
    private ListView<String> cartListView;
    
    @FXML
    private Label totalLabel;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button placeOrderButton;
    
    private BookStoreFacade facade;
    private BookDAO bookDAO = new BookDAO();

    public void setFacade(BookStoreFacade facade) {
        this.facade = facade;
        loadCart();
    }

    private void loadCart() {
        Cart cart = facade.getCustomerCart();
        if (cart == null) return;
        
        ObservableList<String> cartItems = FXCollections.observableArrayList();
        double total = 0.0;
        
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookDAO.getBookByID(entry.getKey());
            if (book != null) {
                int quantity = entry.getValue();
                double itemTotal = book.getPrice() * quantity;
                total += itemTotal;
                
                String itemInfo = String.format("%s x%d - $%.2f", 
                    book.getTitle(), quantity, itemTotal);
                cartItems.add(itemInfo);
            }
        }
        
        cartListView.setItems(cartItems);
        totalLabel.setText(String.format("Total: $%.2f", total));
        
        // Add context menu for removing items
        cartListView.setContextMenu(createContextMenu());
    }

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem removeItem = new MenuItem("Remove from Cart");
        removeItem.setOnAction(event -> {
            int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Cart cart = facade.getCustomerCart();
                Integer bookId = (Integer) cart.getItems().keySet().toArray()[selectedIndex];
                Book book = bookDAO.getBookByID(bookId);
                
                facade.removeBookFromCart(book);
                loadCart();
            }
        });
        
        MenuItem updateQuantity = new MenuItem("Update Quantity");
        updateQuantity.setOnAction(event -> {
            int selectedIndex = cartListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Cart cart = facade.getCustomerCart();
                Integer bookId = (Integer) cart.getItems().keySet().toArray()[selectedIndex];
                Book book = bookDAO.getBookByID(bookId);
                
                // Show dialog for new quantity
                TextInputDialog dialog = new TextInputDialog("1");
                dialog.setTitle("Update Quantity");
                dialog.setHeaderText("Enter new quantity for " + book.getTitle());
                dialog.setContentText("Quantity:");
                
                dialog.showAndWait().ifPresent(quantity -> {
                    try {
                        int newQty = Integer.parseInt(quantity);
                        if (newQty > 0 && newQty <= book.getStock()) {
                            facade.updateCartQuantity(book, newQty);
                            loadCart();
                        } else {
                            showAlert("Invalid Quantity", "Quantity must be between 1 and " + book.getStock(), Alert.AlertType.ERROR);
                        }
                    } catch (NumberFormatException e) {
                        showAlert("Invalid Input", "Please enter a valid number", Alert.AlertType.ERROR);
                    }
                });
            }
        });
        
        contextMenu.getItems().addAll(removeItem, updateQuantity);
        return contextMenu;
    }

    @FXML
    protected void handlePlaceOrderAction() {
        Cart cart = facade.getCustomerCart();
        if (cart == null || cart.getItems().isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty!", Alert.AlertType.WARNING);
            return;
        }
        
        // Confirm order
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Order");
        confirmAlert.setHeaderText("Place Order");
        confirmAlert.setContentText("Are you sure you want to place this order?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    facade.placeOrder();
                    showAlert("Success", "Order placed successfully!", Alert.AlertType.INFORMATION);
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
