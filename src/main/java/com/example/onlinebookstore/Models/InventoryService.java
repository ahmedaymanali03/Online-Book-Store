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
        System.out.println("InventoryService: Order " + order.getId() + " confirmed. Updating stock and popularity...");

        // Update stock and popularity for each item in the order
        for (OrderItem item : items) {
            Book book = bookDAO.getBookByID(item.getBookId());
            if (book != null) {
                // Update stock
                int newStock = book.getStock() - item.getQuantity();
                if (newStock < 0) newStock = 0; // Safety check
                bookDAO.updateBookStock(item.getBookId(), newStock);
                
                // Update popularity (increment by quantity sold)
                int newPopularity = book.getPopularity() + item.getQuantity();
                bookDAO.updateBookPopularity(item.getBookId(), newPopularity);
                
                System.out.println("Updated book ID " + item.getBookId() + 
                                   ": stock (qty: " + item.getQuantity() + ") to " + newStock +
                                   ", popularity to " + newPopularity);
            }
        }
    }
    
    /**
     * Called when an order is reverted (from CONFIRMED/SHIPPED/DELIVERED to PENDING/CANCELED)
     * Restores stock and decrements popularity
     */
    public void onOrderReverted(Order order, List<OrderItem> items) {
        System.out.println("InventoryService: Order " + order.getId() + " reverted. Restoring stock...");

        for (OrderItem item : items) {
            Book book = bookDAO.getBookByID(item.getBookId());
            if (book != null) {
                // Restore stock
                int newStock = book.getStock() + item.getQuantity();
                bookDAO.updateBookStock(item.getBookId(), newStock);
                
                // Decrement popularity
                int newPopularity = book.getPopularity() - item.getQuantity();
                if (newPopularity < 0) newPopularity = 0;
                bookDAO.updateBookPopularity(item.getBookId(), newPopularity);
                
                System.out.println("Restored book ID " + item.getBookId() + 
                                   ": stock to " + newStock +
                                   ", popularity to " + newPopularity);
            }
        }
    }
}
