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

import java.util.List;

public class OrderHistoryController {
    @FXML
    private ListView<String> orderListView;
    
    @FXML
    private TextArea orderDetailsArea;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button leaveReviewButton;
    
    private BookStoreFacade facade;
    private List<Order> orders;
    private OrderItemDAO orderItemDAO = new OrderItemDAO();
    private BookDAO bookDAO = new BookDAO();
    private Order selectedOrder;
    private List<OrderItem> currentOrderItems;

    public void setFacade(BookStoreFacade facade) {
        this.facade = facade;
        loadOrders();
    }

    private void loadOrders() {
        orders = facade.getCustomerOrderHistory();
        if (orders == null) return;
        
        ObservableList<String> orderItems = FXCollections.observableArrayList();
        
        for (Order order : orders) {
            String orderInfo = String.format("Order #%d - %s - $%.2f (%s)", 
                order.getId(), 
                order.getOrderDate().substring(0, Math.min(10, order.getOrderDate().length())),
                order.getTotalPrice(), 
                order.getStatus());
            orderItems.add(orderInfo);
        }
        
        orderListView.setItems(orderItems);
        
        // Add context menu for canceling pending orders
        orderListView.setContextMenu(createContextMenu());
    }

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem cancelOrder = new MenuItem("Cancel Order");
        cancelOrder.setOnAction(event -> {
            int selectedIndex = orderListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Order order = orders.get(selectedIndex);
                
                if (!"PENDING".equals(order.getStatus())) {
                    showAlert("Cannot Cancel", "Only pending orders can be cancelled", Alert.AlertType.WARNING);
                    return;
                }
                
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Cancel Order");
                confirmAlert.setHeaderText("Cancel Order #" + order.getId());
                confirmAlert.setContentText("Are you sure you want to cancel this order?");
                
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        facade.cancelOrder(order.getId());
                        showAlert("Success", "Order cancelled successfully", Alert.AlertType.INFORMATION);
                        loadOrders();
                    }
                });
            }
        });
        
        contextMenu.getItems().add(cancelOrder);
        return contextMenu;
    }

    @FXML
    protected void handleOrderSelection() {
        int selectedIndex = orderListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Order order = orders.get(selectedIndex);
            displayOrderDetails(order);
        }
    }

    private void displayOrderDetails(Order order) {
        this.selectedOrder = order;
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(order.getId()).append("\n");
        details.append("Date: ").append(order.getOrderDate()).append("\n");
        details.append("Status: ").append(order.getStatus()).append("\n");
        details.append("Total: $").append(String.format("%.2f", order.getTotalPrice())).append("\n\n");
        details.append("Items:\n");
        details.append("----------------------------------------\n");
        
        // Get order items
        currentOrderItems = orderItemDAO.getOrderItemsByOrder(order.getId());
        
        for (OrderItem item : currentOrderItems) {
            Book book = bookDAO.getBookByID(item.getBookId());
            if (book != null) {
                details.append(String.format("%s\n", book.getTitle()));
                details.append(String.format("  Quantity: %d x $%.2f = $%.2f\n", 
                    item.getQuantity(), item.getPrice(), item.getQuantity() * item.getPrice()));
                details.append("\n");
            }
        }
        
        orderDetailsArea.setText(details.toString());
        
        // Show review button only for confirmed orders
        leaveReviewButton.setVisible("CONFIRMED".equals(order.getStatus()) && currentOrderItems != null && !currentOrderItems.isEmpty());
    }

    @FXML
    protected void handleLeaveReviewAction() {
        if (selectedOrder == null || currentOrderItems == null || currentOrderItems.isEmpty()) {
            showAlert("No Order Selected", "Please select an order first", Alert.AlertType.WARNING);
            return;
        }
        
        // Create dialog to select which book to review
        ChoiceDialog<String> bookDialog = new ChoiceDialog<>();
        bookDialog.setTitle("Select Book to Review");
        bookDialog.setHeaderText("Choose a book from this order to review:");
        bookDialog.setContentText("Book:");
        
        // Populate book choices
        for (OrderItem item : currentOrderItems) {
            Book book = bookDAO.getBookByID(item.getBookId());
            if (book != null) {
                bookDialog.getItems().add(book.getId() + ": " + book.getTitle());
            }
        }
        
        if (bookDialog.getItems().isEmpty()) {
            showAlert("No Books", "No books found in this order", Alert.AlertType.WARNING);
            return;
        }
        
        bookDialog.setSelectedItem(bookDialog.getItems().get(0));
        
        bookDialog.showAndWait().ifPresent(selectedBookStr -> {
            // Extract book ID from selection
            int bookId = Integer.parseInt(selectedBookStr.split(":")[0]);
            Book selectedBook = bookDAO.getBookByID(bookId);
            
            if (selectedBook != null) {
                showReviewDialog(selectedBook);
            }
        });
    }
    
    private void showReviewDialog(Book book) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Leave Review");
        dialog.setHeaderText("Review: " + book.getTitle());
        
        // Create rating and comment fields
        Label ratingLabel = new Label("Rating (1-5 stars):");
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 5);
        ratingSpinner.setEditable(true);
        
        Label commentLabel = new Label("Your Review:");
        TextArea commentArea = new TextArea();
        commentArea.setPrefRowCount(5);
        commentArea.setWrapText(true);
        commentArea.setPromptText("Share your thoughts about this book...");
        
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(ratingLabel, ratingSpinner, commentLabel, commentArea);
        content.setPadding(new javafx.geometry.Insets(10));
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                int rating = ratingSpinner.getValue();
                String comment = commentArea.getText().trim();
                
                if (comment.isEmpty()) {
                    showAlert("Invalid Review", "Please write a comment for your review", Alert.AlertType.WARNING);
                    return;
                }
                
                facade.addReview(book.getId(), rating, comment);
                showAlert("Success", "Your review has been submitted!", Alert.AlertType.INFORMATION);
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
