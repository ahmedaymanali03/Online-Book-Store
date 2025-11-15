package com.example.onlinebookstore.Models;

import java.util.List;
import java.util.Map;

/**
 * Facade Pattern:
 * Provides a single, simplified interface to the backend subsystems
 * (UserService, BookService, OrderService).
 * * Your JavaFX controllers will interact with this class.
 */
public class BookStoreFacade {
    private UserService userService;
    private BookService bookService;
    private OrderService orderService;
    private InventoryService inventoryService;
    private ReviewDAO reviewDAO;
    private CategoryDAO categoryDAO;
    private BookDAO bookDAO;
    private StatisticsService statisticsService;

    private User currentLoggedInUser;

    public BookStoreFacade() {
        this.userService = new UserService();
        this.bookService = new BookService();
        this.orderService = new OrderService();
        this.inventoryService = new InventoryService();
        this.reviewDAO = new ReviewDAO();
        this.categoryDAO = new CategoryDAO();
        this.bookDAO = new BookDAO();
        this.statisticsService = new StatisticsService();

        // Wire up the Observer
        this.orderService.addObserver(inventoryService);
    }

    // ==================== User Management ====================
    
    public boolean login(String username, String password) {
        this.currentLoggedInUser = userService.login(username, password);
        return this.currentLoggedInUser != null;
    }

    public void logout() {
        this.currentLoggedInUser = null;
    }

    public User getCurrentUser() {
        return currentLoggedInUser;
    }
    
    public void registerCustomer(String username, String password, String address, String phone) {
        userService.registerCustomer(username, password, address, phone);
    }
    
    public boolean isAdmin() {
        return currentLoggedInUser instanceof Admin;
    }
    
    public boolean isCustomer() {
        return currentLoggedInUser instanceof Customer;
    }

    // ==================== Book Browsing & Search (Customer) ====================
    
    public List<Book> getAllBooks() {
        return bookService.getBooks(null);
    }
    
    public List<Book> getBooksSortedByPrice() {
        return bookService.getBooks(new SortByPrice());
    }

    public List<Book> getBooksSortedByPopularity() {
        return bookService.getBooks(new SortByPopularity());
    }
    
    public List<Book> searchBooksByTitle(String title) {
        return bookDAO.searchByTitle(title);
    }
    
    public List<Book> searchBooksByAuthor(String author) {
        return bookDAO.searchByAuthor(author);
    }
    
    public List<Book> filterBooksByCategory(String category) {
        return bookDAO.filterByCategory(category);
    }
    
    public Book getBookById(int id) {
        return bookDAO.getBookByID(id);
    }

    // ==================== Cart Management (Customer) ====================
    
    public void addBookToCart(Book book, int quantity) {
        if (currentLoggedInUser instanceof Customer) {
            ((Customer) currentLoggedInUser).getCart().addBook(book, quantity);
        }
    }
    
    public void removeBookFromCart(Book book) {
        if (currentLoggedInUser instanceof Customer) {
            ((Customer) currentLoggedInUser).getCart().removeBook(book);
        }
    }
    
    public void updateCartQuantity(Book book, int quantity) {
        if (currentLoggedInUser instanceof Customer) {
            ((Customer) currentLoggedInUser).getCart().updateQuantity(book, quantity);
        }
    }

    public Cart getCustomerCart() {
        if (currentLoggedInUser instanceof Customer) {
            return ((Customer) currentLoggedInUser).getCart();
        }
        return null;
    }
    
    public void clearCart() {
        if (currentLoggedInUser instanceof Customer) {
            ((Customer) currentLoggedInUser).getCart().clear();
        }
    }

    // ==================== Order Management (Customer) ====================
    
    public void placeOrder() {
        if (currentLoggedInUser instanceof Customer) {
            Customer customer = (Customer) currentLoggedInUser;
            orderService.placeOrder(customer, customer.getCart());
        }
    }
    
    public void cancelOrder(int orderId) {
        if (currentLoggedInUser instanceof Customer) {
            orderService.cancelOrder(orderId);
        }
    }
    
    public List<Order> getCustomerOrderHistory() {
        if (currentLoggedInUser instanceof Customer) {
            return orderService.getCustomerOrders(currentLoggedInUser.getId());
        }
        return null;
    }

    // ==================== Review Management (Customer) ====================
    
    public void addReview(int bookId, int rating, String comment) {
        if (currentLoggedInUser instanceof Customer) {
            String reviewDate = java.time.LocalDateTime.now().toString();
            Review review = new Review(bookId, currentLoggedInUser.getId(), rating, comment, reviewDate);
            reviewDAO.addReview(review);
        }
    }
    
    public List<Review> getBookReviews(int bookId) {
        return reviewDAO.getReviewsByBook(bookId);
    }
    
    public List<Review> getCustomerReviews() {
        if (currentLoggedInUser != null) {
            return reviewDAO.getReviewsByCustomer(currentLoggedInUser.getId());
        }
        return null;
    }
    
    public double getBookAverageRating(int bookId) {
        return reviewDAO.getAverageRating(bookId);
    }

    // ==================== Book Management (Admin) ====================
    
    public void addBook(Book book) {
        if (currentLoggedInUser instanceof Admin) {
            bookDAO.addBook(book);
        }
    }
    
    public void updateBook(Book book) {
        if (currentLoggedInUser instanceof Admin) {
            bookDAO.updateBook(book);
        }
    }
    
    public void deleteBook(int bookId) {
        if (currentLoggedInUser instanceof Admin) {
            bookDAO.deleteBook(bookId);
        }
    }
    
    public void updateBookStock(int bookId, int newStock) {
        if (currentLoggedInUser instanceof Admin) {
            bookDAO.updateBookStock(bookId, newStock);
        }
    }

    // ==================== Category Management (Admin) ====================
    
    public void addCategory(String name, String description) {
        if (currentLoggedInUser instanceof Admin) {
            Category category = new Category(name, description);
            categoryDAO.addCategory(category);
        }
    }
    
    public void updateCategory(Category category) {
        if (currentLoggedInUser instanceof Admin) {
            categoryDAO.updateCategory(category);
        }
    }
    
    public void deleteCategory(int categoryId) {
        if (currentLoggedInUser instanceof Admin) {
            categoryDAO.deleteCategory(categoryId);
        }
    }
    
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    // ==================== Order Management (Admin) ====================
    
    public List<Order> getAllOrders() {
        if (currentLoggedInUser instanceof Admin) {
            return orderService.getAllOrders();
        }
        return null;
    }
    
    public void confirmOrder(Order order) {
        if (currentLoggedInUser instanceof Admin) {
            orderService.confirmOrder(order);
        }
    }
    
    public void cancelOrderAdmin(int orderId) {
        if (currentLoggedInUser instanceof Admin) {
            orderService.cancelOrder(orderId);
        }
    }
    
    public void updateOrderStatus(int orderId, String status) {
        if (currentLoggedInUser instanceof Admin) {
            orderService.confirmOrder(orderService.getAllOrders().stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .orElse(null));
        }
    }

    // ==================== Inventory & Statistics (Admin) ====================
    
    public List<Book> getTopSellingBooks(int limit) {
        if (currentLoggedInUser instanceof Admin) {
            return bookDAO.getTopSellingBooks(limit);
        }
        return null;
    }
    
    public List<Book> getLowStockBooks(int threshold) {
        if (currentLoggedInUser instanceof Admin) {
            return bookDAO.getLowStockBooks(threshold);
        }
        return null;
    }
    
    public double getTotalRevenue() {
        if (currentLoggedInUser instanceof Admin) {
            return statisticsService.getTotalRevenue();
        }
        return 0.0;
    }
    
    public int getTotalOrders() {
        if (currentLoggedInUser instanceof Admin) {
            return statisticsService.getTotalOrders();
        }
        return 0;
    }
    
    public Map<String, Double> getSalesByCategory() {
        if (currentLoggedInUser instanceof Admin) {
            return statisticsService.getSalesByCategory();
        }
        return null;
    }
    
    public String getMostPopularCategory() {
        if (currentLoggedInUser instanceof Admin) {
            return statisticsService.getMostPopularCategory();
        }
        return null;
    }
    
    public Map<Integer, Integer> getBooksSoldCount() {
        if (currentLoggedInUser instanceof Admin) {
            return statisticsService.getBooksSoldCount();
        }
        return null;
    }
    
    public Map<String, Integer> getOrdersByStatus() {
        if (currentLoggedInUser instanceof Admin) {
            return statisticsService.getOrdersByStatus();
        }
        return null;
    }
}