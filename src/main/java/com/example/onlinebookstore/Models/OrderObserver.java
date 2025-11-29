package com.example.onlinebookstore.Models;

import java.util.List;

public interface OrderObserver {
    void onOrderConfirmed(Order order);
    
    // New method with order items for proper stock updates
    default void onOrderConfirmed(Order order, List<OrderItem> items) {
        onOrderConfirmed(order); // Default to legacy behavior
    }
}
