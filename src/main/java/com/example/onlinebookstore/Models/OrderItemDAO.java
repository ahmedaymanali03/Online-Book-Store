package com.example.onlinebookstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for OrderItem operations.
 */
public class OrderItemDAO {
    private Connection conn;

    public OrderItemDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public void addOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_items (orderId, bookId, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, item.getOrderId());
            pstmt.setInt(2, item.getBookId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getPrice());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                item.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<OrderItem> getOrderItemsByOrder(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE orderId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("orderId"),
                    rs.getInt("bookId"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                );
                item.setId(rs.getInt("id"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void deleteOrderItems(int orderId) {
        String sql = "DELETE FROM order_items WHERE orderId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
