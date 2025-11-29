package com.example.onlinebookstore.Models;

import java.util.List;

/**
 * Observer Implementation:
 * Listens for order confirmations to update stock.
 */
public class InventoryService implements OrderObserver {
    private BookDAO bookDAO;

    public InventoryService() {
        this.bookDAO = new BookDAO();
    }

    @Override
    public void onOrderConfirmed(Order order) {
        // Legacy method - does nothing, use onOrderConfirmed with items
        System.out.println("InventoryService: Order " + order.getId() + " confirmed (no items provided)");
    }
    
    @Override
    public void onOrderConfirmed(Order order, List<OrderItem> items) {
        System.out.println("InventoryService: Order " + order.getId() + " confirmed. Updating stock...");

        // Update stock for each item in the order
        for (OrderItem item : items) {
            Book book = bookDAO.getBookByID(item.getBookId());
            if (book != null) {
                int newStock = book.getStock() - item.getQuantity();
                if (newStock < 0) newStock = 0; // Safety check
                bookDAO.updateBookStock(item.getBookId(), newStock);
                System.out.println("Updated stock for book ID " + item.getBookId() + 
                                   " (qty: " + item.getQuantity() + ") to " + newStock);
            }
        }
    }
}
