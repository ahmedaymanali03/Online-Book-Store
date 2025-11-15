package com.example.onlinebookstore.Models;

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
        System.out.println("InventoryService: Order " + order.getId() + " confirmed. Updating stock...");

        // Update stock for each book in the order
        if (order.getBooks() != null) {
            for (Book book : order.getBooks()) {
                Book currentBook = bookDAO.getBookByID(book.getId());
                if (currentBook != null) {
                    int newStock = currentBook.getStock() - 1; // Assuming quantity of 1 per book
                    bookDAO.updateBookStock(book.getId(), newStock);
                    System.out.println("Updated stock for book ID " + book.getId() + " to " + newStock);
                }
            }
        }
    }
}
