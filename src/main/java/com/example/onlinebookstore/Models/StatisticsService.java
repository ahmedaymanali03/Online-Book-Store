package com.example.onlinebookstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating sales statistics and reports
 */
public class StatisticsService {
    private Connection conn;

    public StatisticsService() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    /**
     * Get total sales revenue
     */
    public double getTotalRevenue() {
        String sql = "SELECT SUM(totalPrice) as revenue FROM orders WHERE UPPER(status) != 'CANCELED'";
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Get total number of orders
     */
    public int getTotalOrders() {
        String sql = "SELECT COUNT(*) as count FROM orders WHERE UPPER(status) != 'CANCELED'";
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get sales by category
     */
    public Map<String, Double> getSalesByCategory() {
        Map<String, Double> categoryMap = new HashMap<>();
        String sql = "SELECT b.category, SUM(oi.quantity * oi.price) as total " +
                     "FROM order_items oi " +
                     "JOIN books b ON oi.bookId = b.id " +
                     "JOIN orders o ON oi.orderId = o.id " +
                     "WHERE UPPER(o.status) != 'CANCELED' AND UPPER(o.status) != 'PENDING' " +
                     "GROUP BY b.category";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categoryMap.put(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryMap;
    }

    /**
     * Get most popular category by sales count
     */
    public String getMostPopularCategory() {
        String sql = "SELECT b.category, COUNT(*) as count " +
                     "FROM order_items oi " +
                     "JOIN books b ON oi.bookId = b.id " +
                     "JOIN orders o ON oi.orderId = o.id " +
                     "WHERE UPPER(o.status) != 'CANCELED' " +
                     "GROUP BY b.category " +
                     "ORDER BY count DESC LIMIT 1";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("category");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get number of books sold by book ID
     */
    public Map<Integer, Integer> getBooksSoldCount() {
        Map<Integer, Integer> bookMap = new HashMap<>();
        String sql = "SELECT bookId, SUM(quantity) as total " +
                     "FROM order_items oi " +
                     "JOIN orders o ON oi.orderId = o.id " +
                     "WHERE UPPER(o.status) != 'CANCELED' " +
                     "GROUP BY bookId";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookMap.put(rs.getInt("bookId"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookMap;
    }

    /**
     * Get orders by status (normalized to uppercase)
     */
    public Map<String, Integer> getOrdersByStatus() {
        Map<String, Integer> statusMap = new HashMap<>();
        String sql = "SELECT UPPER(status) as status, COUNT(*) as count FROM orders GROUP BY UPPER(status)";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                statusMap.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statusMap;
    }

    /**
     * Get revenue for a specific time period
     */
    public double getRevenueByDateRange(String startDate, String endDate) {
        String sql = "SELECT SUM(totalPrice) as revenue FROM orders " +
                     "WHERE UPPER(status) != 'CANCELED' AND orderDate BETWEEN ? AND ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Get total number of books sold (sum of all quantities in confirmed orders)
     */
    public int getTotalBooksSold() {
        String sql = "SELECT SUM(oi.quantity) as total " +
                     "FROM order_items oi " +
                     "JOIN orders o ON oi.orderId = o.id " +
                     "WHERE UPPER(o.status) != 'CANCELED' AND UPPER(o.status) != 'PENDING'";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total number of books in inventory
     */
    public int getTotalBooksInInventory() {
        String sql = "SELECT COUNT(*) as count FROM books";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get book count by category
     */
    public Map<String, Integer> getBookCountByCategory() {
        Map<String, Integer> categoryMap = new HashMap<>();
        String sql = "SELECT category, COUNT(*) as count FROM books GROUP BY category";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categoryMap.put(rs.getString("category"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryMap;
    }

    /**
     * Get total stock across all books
     */
    public int getTotalStock() {
        String sql = "SELECT SUM(stock) as total FROM books";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get number of categories
     */
    public int getTotalCategories() {
        String sql = "SELECT COUNT(*) as count FROM categories";
        
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Recalculate popularity for all books based on actual order_items data
     * Only counts items from confirmed orders (not PENDING or CANCELED)
     */
    public void recalculateAllPopularity() {
        // First, reset all popularity to 0
        String resetSql = "UPDATE books SET popularity = 0";
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(resetSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Then calculate actual popularity from order_items
        String calcSql = "SELECT oi.bookId, SUM(oi.quantity) as totalSold " +
                         "FROM order_items oi " +
                         "JOIN orders o ON oi.orderId = o.id " +
                         "WHERE UPPER(o.status) != 'CANCELED' AND UPPER(o.status) != 'PENDING' " +
                         "GROUP BY oi.bookId";
        
        String updateSql = "UPDATE books SET popularity = ? WHERE id = ?";
        
        try (var stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(calcSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            while (rs.next()) {
                int bookId = rs.getInt("bookId");
                int totalSold = rs.getInt("totalSold");
                
                updateStmt.setInt(1, totalSold);
                updateStmt.setInt(2, bookId);
                updateStmt.executeUpdate();
                
                System.out.println("Updated book ID " + bookId + " popularity to " + totalSold);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("Popularity recalculation complete!");
    }
}
