package com.example.onlinebookstore.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for order logic.
 * Implements Subject for the Observer pattern.
 */
public class OrderService implements OrderSubject {
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private List<OrderObserver> observers = new ArrayList<>();

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
    }

    public boolean placeOrder(Customer customer, Cart cart) {
        // 1. First validate stock availability
        BookDAO bookDAO = new BookDAO();
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookDAO.getBookByID(entry.getKey());
            if (book == null || book.getStock() < entry.getValue()) {
                System.out.println("Insufficient stock for book: " + (book != null ? book.getTitle() : "Unknown"));
                return false; // Order failed - insufficient stock
            }
        }
        
        // 2. Calculate total price from cart
        double totalPrice = 0.0;
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookDAO.getBookByID(entry.getKey());
            if (book != null) {
                totalPrice += book.getPrice() * entry.getValue();
            }
        }
        
        // 3. Create Order object from cart
        String orderDate = java.time.LocalDateTime.now().toString();
        Order order = new Order(customer.getId(), orderDate, "PENDING", totalPrice);
        
        // 4. Save order to DB via OrderDAO
        orderDAO.createOrder(order);
        
        // 5. Save order items
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookDAO.getBookByID(entry.getKey());
            if (book != null) {
                OrderItem item = new OrderItem(order.getId(), entry.getKey(), entry.getValue(), book.getPrice());
                orderItemDAO.addOrderItem(item);
            }
        }
        
        // 6. Clear the customer's cart
        cart.clear();
        
        System.out.println("Order placed for " + customer.getUsername() + " with ID: " + order.getId());
        return true; // Order successful
    }
    
    public void cancelOrder(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order != null && order.getStatus().equals("PENDING")) {
            orderDAO.cancelOrder(orderId);
            System.out.println("Order " + orderId + " cancelled successfully");
        } else {
            System.out.println("Order cannot be cancelled (already confirmed or shipped)");
        }
    }
    
    public void updateOrderStatus(int orderId, String newStatus) {
        orderDAO.updateOrderStatus(orderId, newStatus);
    }
    
    public List<Order> getCustomerOrders(int customerId) {
        return orderDAO.getOrdersByCustomer(customerId);
    }
    
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public boolean confirmOrder(Order order) {
        // 1. First validate stock availability for all items
        List<OrderItem> items = orderItemDAO.getOrderItemsByOrder(order.getId());
        BookDAO bookDAO = new BookDAO();
        
        for (OrderItem item : items) {
            Book book = bookDAO.getBookByID(item.getBookId());
            if (book == null || book.getStock() < item.getQuantity()) {
                System.out.println("Cannot confirm order - insufficient stock for book ID: " + item.getBookId());
                return false; // Cannot confirm - insufficient stock
            }
        }
        
        // 2. Update order status to 'CONFIRMED' via OrderDAO
        order.setStatus("CONFIRMED");
        orderDAO.updateOrderStatus(order.getId(), "CONFIRMED");

        // 3. Notify all observers (e.g., InventoryService) to update stock
        // Pass the order items so InventoryService knows quantities
        notifyObservers(order, items);
        
        return true; // Order confirmed successfully
    }
    
    // Overload for backward compatibility
    public void notifyObservers(Order order, List<OrderItem> items) {
        for (OrderObserver observer : observers) {
            observer.onOrderConfirmed(order, items);
        }
    }

    @Override
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderConfirmed(order);
        }
    }
}