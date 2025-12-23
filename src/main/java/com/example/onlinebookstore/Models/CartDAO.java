package com.example.onlinebookstore.Models;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object for Cart persistence.
 * Handles saving and loading cart items from the database.
 */
public class CartDAO {
    private Connection conn;

    public CartDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Load cart items for a customer from the database
     */
    public Cart loadCart(int customerId) {
        Cart cart = new Cart();
        String sql = "SELECT bookId, quantity FROM cart_items WHERE customerId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int bookId = rs.getInt("bookId");
                int quantity = rs.getInt("quantity");
                cart.getItems().put(bookId, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cart;
    }

    /**
     * Save or update a cart item for a customer
     */
    public void saveCartItem(int customerId, int bookId, int quantity) {
        if (quantity <= 0) {
            removeCartItem(customerId, bookId);
            return;
        }
        
        // Use INSERT OR REPLACE to handle both insert and update
        String sql = "INSERT OR REPLACE INTO cart_items (customerId, bookId, quantity) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove a cart item
     */
    public void removeCartItem(int customerId, int bookId) {
        String sql = "DELETE FROM cart_items WHERE customerId = ? AND bookId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear all cart items for a customer
     */
    public void clearCart(int customerId) {
        String sql = "DELETE FROM cart_items WHERE customerId = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
