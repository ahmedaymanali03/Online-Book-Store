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

    public void placeOrder(Customer customer, Cart cart) {
        // 1. Calculate total price from cart
        double totalPrice = 0.0;
        BookDAO bookDAO = new BookDAO();
        
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookDAO.getBookByID(entry.getKey());
            if (book != null) {
                totalPrice += book.getPrice() * entry.getValue();
            }
        }
        
        // 2. Create Order object from cart
        String orderDate = java.time.LocalDateTime.now().toString();
        Order order = new Order(customer.getId(), orderDate, "PENDING", totalPrice);
        
        // 3. Save order to DB via OrderDAO
        orderDAO.createOrder(order);
        
        // 4. Save order items
        for (Map.Entry<Integer, Integer> entry : cart.getItems().entrySet()) {
            Book book = bookDAO.getBookByID(entry.getKey());
            if (book != null) {
                OrderItem item = new OrderItem(order.getId(), entry.getKey(), entry.getValue(), book.getPrice());
                orderItemDAO.addOrderItem(item);
            }
        }
        
        // 5. Clear the customer's cart
        cart.clear();
        
        System.out.println("Order placed for " + customer.getUsername() + " with ID: " + order.getId());
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
    
    public List<Order> getCustomerOrders(int customerId) {
        return orderDAO.getOrdersByCustomer(customerId);
    }
    
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public void confirmOrder(Order order) {
        // 1. Update order status to 'CONFIRMED' via OrderDAO
        order.setStatus("CONFIRMED");
        orderDAO.updateOrderStatus(order.getId(), "CONFIRMED");

        // 2. Notify all observers (e.g., InventoryService)
        notifyObservers(order);
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