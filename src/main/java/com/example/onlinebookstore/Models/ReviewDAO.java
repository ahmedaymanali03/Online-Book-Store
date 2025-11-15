package com.example.onlinebookstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Review operations.
 */
public class ReviewDAO {
    private Connection conn;

    public ReviewDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public void addReview(Review review) {
        String sql = "INSERT INTO reviews (bookId, customerId, rating, comment, reviewDate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, review.getBookId());
            pstmt.setInt(2, review.getCustomerId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.setString(5, review.getReviewDate());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                review.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Review> getReviewsByBook(int bookId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE bookId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("bookId"),
                    rs.getInt("customerId"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getString("reviewDate")
                );
                review.setId(rs.getInt("id"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getReviewsByCustomer(int customerId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE customerId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("bookId"),
                    rs.getInt("customerId"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getString("reviewDate")
                );
                review.setId(rs.getInt("id"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public void deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getAverageRating(int bookId) {
        String sql = "SELECT AVG(rating) as avgRating FROM reviews WHERE bookId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("avgRating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
