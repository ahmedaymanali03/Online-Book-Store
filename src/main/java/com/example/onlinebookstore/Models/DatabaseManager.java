package com.example.onlinebookstore.Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Pattern:
 * Manages a single, shared connection to the SQLite database.
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;
    private final String dbUrl = "jdbc:sqlite:bookstore.db"; // Database file name

    // Private constructor to prevent instantiation
    private DatabaseManager() {
        try {
            this.connection = DriverManager.getConnection(dbUrl);
            System.out.println("Database connection established.");
            createTables(); // Helper to create tables if they don't exist
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    // Public method to get the single instance
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // A helper method to initialize the DB schema
    private void createTables() {
        String userTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL,"
                + "address TEXT,"
                + "phone TEXT,"
                + "role TEXT NOT NULL" // 'CUSTOMER' or 'ADMIN'
                + ");";

        String bookTable = "CREATE TABLE IF NOT EXISTS books ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "author TEXT NOT NULL,"
                + "price REAL NOT NULL,"
                + "stock INTEGER NOT NULL,"
                + "category TEXT,"
                + "popularity INTEGER DEFAULT 0,"
                + "edition TEXT,"
                + "coverImage TEXT"
                + ");";

        String orderTable = "CREATE TABLE IF NOT EXISTS orders ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "customerId INTEGER NOT NULL,"
                + "orderDate TEXT NOT NULL,"
                + "status TEXT NOT NULL," // 'PENDING', 'CONFIRMED', 'SHIPPED', 'CANCELED'
                + "totalPrice REAL NOT NULL,"
                + "FOREIGN KEY (customerId) REFERENCES users(id)"
                + ");";
        
        String orderItemTable = "CREATE TABLE IF NOT EXISTS order_items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "orderId INTEGER NOT NULL,"
                + "bookId INTEGER NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "price REAL NOT NULL,"
                + "FOREIGN KEY (orderId) REFERENCES orders(id),"
                + "FOREIGN KEY (bookId) REFERENCES books(id)"
                + ");";
        
        String categoryTable = "CREATE TABLE IF NOT EXISTS categories ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT UNIQUE NOT NULL,"
                + "description TEXT"
                + ");";
        
        String reviewTable = "CREATE TABLE IF NOT EXISTS reviews ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "bookId INTEGER NOT NULL,"
                + "customerId INTEGER NOT NULL,"
                + "rating INTEGER NOT NULL," // 1-5 stars
                + "comment TEXT,"
                + "reviewDate TEXT NOT NULL,"
                + "FOREIGN KEY (bookId) REFERENCES books(id),"
                + "FOREIGN KEY (customerId) REFERENCES users(id)"
                + ");";
        
        try (var stmt = connection.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(categoryTable);
            stmt.execute(bookTable);
            stmt.execute(orderTable);
            stmt.execute(orderItemTable);
            stmt.execute(reviewTable);
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
}