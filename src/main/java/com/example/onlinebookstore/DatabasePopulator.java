package com.example.onlinebookstore;

import com.example.onlinebookstore.Models.*;

import java.util.List;

/**
 * Utility class to populate the database with sample data for testing
 */
public class DatabasePopulator {
    
    public static void populateDatabase() {
        System.out.println("Starting database population...");
        
        // Create categories
        CategoryDAO categoryDAO = new CategoryDAO();
        Category fiction = new Category("Fiction", "Fiction books");
        Category nonFiction = new Category("Non-Fiction", "Non-fiction books");
        Category science = new Category("Science", "Science books");
        Category technology = new Category("Technology", "Technology books");
        Category history = new Category("History", "History books");
        
        categoryDAO.addCategory(fiction);
        categoryDAO.addCategory(nonFiction);
        categoryDAO.addCategory(science);
        categoryDAO.addCategory(technology);
        categoryDAO.addCategory(history);
        System.out.println("✓ Categories created");
        
        // Create books
        BookDAO bookDAO = new BookDAO();
        
        Book[] books = {
            new Book(0, "The Great Gatsby", "F. Scott Fitzgerald", 10.99, 50, "Fiction", 100),
            new Book(0, "To Kill a Mockingbird", "Harper Lee", 12.99, 45, "Fiction", 95),
            new Book(0, "1984", "George Orwell", 13.99, 60, "Fiction", 120),
            new Book(0, "Pride and Prejudice", "Jane Austen", 9.99, 35, "Fiction", 85),
            new Book(0, "The Hobbit", "J.R.R. Tolkien", 14.99, 40, "Fiction", 110),
            
            new Book(0, "Sapiens", "Yuval Noah Harari", 16.99, 30, "Non-Fiction", 90),
            new Book(0, "Educated", "Tara Westover", 15.99, 25, "Non-Fiction", 75),
            new Book(0, "Thinking, Fast and Slow", "Daniel Kahneman", 17.99, 20, "Non-Fiction", 70),
            new Book(0, "The Immortal Life of Henrietta Lacks", "Rebecca Skloot", 14.99, 28, "Non-Fiction", 65),
            
            new Book(0, "A Brief History of Time", "Stephen Hawking", 18.99, 22, "Science", 80),
            new Book(0, "The Selfish Gene", "Richard Dawkins", 15.99, 18, "Science", 60),
            new Book(0, "Cosmos", "Carl Sagan", 16.99, 24, "Science", 75),
            new Book(0, "The Origin of Species", "Charles Darwin", 12.99, 15, "Science", 55),
            
            new Book(0, "Clean Code", "Robert C. Martin", 42.99, 50, "Technology", 150),
            new Book(0, "The Pragmatic Programmer", "Andrew Hunt", 39.99, 45, "Technology", 140),
            new Book(0, "Design Patterns", "Gang of Four", 44.99, 30, "Technology", 120),
            new Book(0, "Introduction to Algorithms", "Cormen et al.", 89.99, 25, "Technology", 100),
            new Book(0, "JavaScript: The Good Parts", "Douglas Crockford", 29.99, 35, "Technology", 90),
            
            new Book(0, "The Guns of August", "Barbara Tuchman", 16.99, 20, "History", 60),
            new Book(0, "SPQR", "Mary Beard", 18.99, 22, "History", 65),
            new Book(0, "The Wright Brothers", "David McCullough", 15.99, 18, "History", 55)
        };
        
        for (Book book : books) {
            bookDAO.addBook(book);
        }
        System.out.println("✓ " + books.length + " books created");
        
        // Create admin user
        UserDAO userDAO = new UserDAO();
        Admin admin = new Admin("admin", "admin123");
        userDAO.createUser(admin);
        System.out.println("✓ Admin user created (username: admin, password: admin123)");
        
        // Create customer users
        Customer customer1 = new Customer("john_doe", "password123", "123 Main St", "555-0101");
        Customer customer2 = new Customer("jane_smith", "password456", "456 Oak Ave", "555-0102");
        Customer customer3 = new Customer("bob_wilson", "password789", "789 Pine Rd", "555-0103");
        
        userDAO.createUser(customer1);
        userDAO.createUser(customer2);
        userDAO.createUser(customer3);
        System.out.println("✓ 3 customer users created");
        System.out.println("  - john_doe / password123");
        System.out.println("  - jane_smith / password456");
        System.out.println("  - bob_wilson / password789");
        
        // Create some reviews
        ReviewDAO reviewDAO = new ReviewDAO();
        List<Book> allBooks = bookDAO.getAllBooks();
        
        if (allBooks.size() >= 5) {
            Review review1 = new Review(allBooks.get(0).getId(), 1, 5, "Amazing book! Highly recommend.", "2024-11-10");
            Review review2 = new Review(allBooks.get(0).getId(), 2, 4, "Great read, loved it.", "2024-11-11");
            Review review3 = new Review(allBooks.get(1).getId(), 1, 5, "A timeless classic.", "2024-11-09");
            Review review4 = new Review(allBooks.get(2).getId(), 3, 5, "Thought-provoking and relevant.", "2024-11-08");
            Review review5 = new Review(allBooks.get(4).getId(), 2, 4, "Beautiful illustrated edition!", "2024-11-07");
            
            reviewDAO.addReview(review1);
            reviewDAO.addReview(review2);
            reviewDAO.addReview(review3);
            reviewDAO.addReview(review4);
            reviewDAO.addReview(review5);
            System.out.println("✓ 5 reviews created");
        }
        
        // Create some sample orders
        OrderDAO orderDAO = new OrderDAO();
        OrderItemDAO orderItemDAO = new OrderItemDAO();
        
        if (allBooks.size() >= 3) {
            // Order 1 for john_doe
            Order order1 = new Order(1, "2024-11-10", "Delivered", 25.98);
            orderDAO.createOrder(order1);
            int orderId1 = order1.getId();
            if (orderId1 > 0) {
                orderItemDAO.addOrderItem(new OrderItem(orderId1, allBooks.get(0).getId(), 1, allBooks.get(0).getPrice()));
                orderItemDAO.addOrderItem(new OrderItem(orderId1, allBooks.get(1).getId(), 1, allBooks.get(1).getPrice()));
            }
            
            // Order 2 for jane_smith
            Order order2 = new Order(2, "2024-11-12", "Shipped", 42.99);
            orderDAO.createOrder(order2);
            int orderId2 = order2.getId();
            if (orderId2 > 0) {
                orderItemDAO.addOrderItem(new OrderItem(orderId2, allBooks.get(14).getId(), 1, allBooks.get(14).getPrice()));
            }
            
            // Order 3 for john_doe (pending)
            Order order3 = new Order(1, "2024-11-14", "Pending", 16.99);
            orderDAO.createOrder(order3);
            int orderId3 = order3.getId();
            if (orderId3 > 0) {
                orderItemDAO.addOrderItem(new OrderItem(orderId3, allBooks.get(5).getId(), 1, allBooks.get(5).getPrice()));
            }
            
            System.out.println("✓ 3 sample orders created");
        }
        
        System.out.println("\n✅ Database population completed successfully!");
        System.out.println("\nTest Accounts:");
        System.out.println("Admin: admin / admin123");
        System.out.println("Customer: john_doe / password123");
        System.out.println("Customer: jane_smith / password456");
        System.out.println("Customer: bob_wilson / password789");
    }
    
    public static void main(String[] args) {
        populateDatabase();
    }
}
