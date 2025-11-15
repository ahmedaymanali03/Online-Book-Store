package com.example.onlinebookstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO for User operations.
 */
public class UserDAO {
    private Connection conn;

    public UserDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Use the Factory to create the right user type
                User user = UserFactory.createUser(
                        rs.getString("role"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("address"),
                        rs.getString("phone")
                );
                user.setId(rs.getInt("id"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createUser(User user) {
        String sql = "INSERT INTO users (username, password, address, phone, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                pstmt.setString(3, customer.getAddress());
                pstmt.setString(4, customer.getPhone());
            } else {
                pstmt.setString(3, null);
                pstmt.setString(4, null);
            }
            
            pstmt.setString(5, user.getRole());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, address = ?, phone = ?, role = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                pstmt.setString(3, customer.getAddress());
                pstmt.setString(4, customer.getPhone());
            } else {
                pstmt.setString(3, null);
                pstmt.setString(4, null);
            }
            
            pstmt.setString(5, user.getRole());
            pstmt.setInt(6, user.getId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Other methods: createUser, updateUser, etc.
}