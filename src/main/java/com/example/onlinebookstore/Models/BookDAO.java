package com.example.onlinebookstore.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Book operations.
 */
public class BookDAO {
    private Connection conn;

    public BookDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public Book getBookByID(int id){
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category"),
                    rs.getInt("popularity"),
                    rs.getString("edition"),
                    rs.getString("coverImage")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (var stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("category"),
                        rs.getInt("popularity"),
                        rs.getString("edition"),
                        rs.getString("coverImage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public void updateBookStock(int bookId, int newStock) {
        String sql = "UPDATE books SET stock = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newStock);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void addBook(Book book) {
        String sql = "INSERT INTO books (title, author, price, stock, category, popularity, edition, coverImage) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setDouble(3, book.getPrice());
            pstmt.setInt(4, book.getStock());
            pstmt.setString(5, book.getCategory());
            pstmt.setInt(6, book.getPopularity());
            pstmt.setString(7, book.getEdition());
            pstmt.setString(8, book.getCoverImage());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, price = ?, stock = ?, category = ?, popularity = ?, edition = ?, coverImage = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setDouble(3, book.getPrice());
            pstmt.setInt(4, book.getStock());
            pstmt.setString(5, book.getCategory());
            pstmt.setInt(6, book.getPopularity());
            pstmt.setString(7, book.getEdition());
            pstmt.setString(8, book.getCoverImage());
            pstmt.setInt(9, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Search and Filter methods
    public List<Book> searchByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category"),
                    rs.getInt("popularity"),
                    rs.getString("edition"),
                    rs.getString("coverImage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public List<Book> searchByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + author + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category"),
                    rs.getInt("popularity"),
                    rs.getString("edition"),
                    rs.getString("coverImage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public List<Book> filterByCategory(String category) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE category = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category"),
                    rs.getInt("popularity"),
                    rs.getString("edition"),
                    rs.getString("coverImage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public List<Book> getTopSellingBooks(int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY popularity DESC LIMIT ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category"),
                    rs.getInt("popularity"),
                    rs.getString("edition"),
                    rs.getString("coverImage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
    
    public List<Book> getLowStockBooks(int threshold) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE stock <= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getString("category"),
                    rs.getInt("popularity"),
                    rs.getString("edition"),
                    rs.getString("coverImage")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

}