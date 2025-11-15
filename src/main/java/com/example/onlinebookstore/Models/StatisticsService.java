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
        String sql = "SELECT SUM(totalPrice) as revenue FROM orders WHERE status != 'CANCELED'";
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
        String sql = "SELECT COUNT(*) as count FROM orders WHERE status != 'CANCELED'";
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
                     "WHERE o.status != 'CANCELED' " +
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
                     "WHERE o.status != 'CANCELED' " +
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
                     "WHERE o.status != 'CANCELED' " +
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
     * Get orders by status
     */
    public Map<String, Integer> getOrdersByStatus() {
        Map<String, Integer> statusMap = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM orders GROUP BY status";
        
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
                     "WHERE status != 'CANCELED' AND orderDate BETWEEN ? AND ?";
        
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
}
