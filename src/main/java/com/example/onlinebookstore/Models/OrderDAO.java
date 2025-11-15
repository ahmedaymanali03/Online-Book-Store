package com.example.onlinebookstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Order operations.
 */
public class OrderDAO {
    private Connection conn;

    public OrderDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public void createOrder(Order order) {
        String sql = "INSERT INTO orders (customerId, orderDate, status, totalPrice) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setString(2, order.getOrderDate());
            pstmt.setString(3, order.getStatus());
            pstmt.setDouble(4, order.getTotalPrice());
            pstmt.executeUpdate();
            
            // Get the generated order ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                order.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customerId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("customerId"),
                    rs.getString("orderDate"),
                    rs.getString("status"),
                    rs.getDouble("totalPrice")
                );
                order.setId(rs.getInt("id"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order(
                    rs.getInt("customerId"),
                    rs.getString("orderDate"),
                    rs.getString("status"),
                    rs.getDouble("totalPrice")
                );
                order.setId(rs.getInt("id"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY orderDate DESC";
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("customerId"),
                    rs.getString("orderDate"),
                    rs.getString("status"),
                    rs.getDouble("totalPrice")
                );
                order.setId(rs.getInt("id"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public void cancelOrder(int orderId) {
        updateOrderStatus(orderId, "CANCELED");
    }
    
    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Other methods: getOrdersByCustomer, getOrderById, etc.
}