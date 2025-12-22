package com.example.onlinebookstore.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Service for book-related logic.
 * Uses the Strategy pattern for sorting.
 */
public class BookService {
    private BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    public List<Book> getBooks(SortStrategy sortStrategy) {
        List<Book> books = bookDAO.getAllBooks();
        // Apply the chosen strategy
        if (sortStrategy != null) {
            sortStrategy.sort(books);
        }
        return books;
    }

    /**
     * Search books by title using SQL LIKE
     */
    public List<Book> searchBooksByTitle(String title) {
        return bookDAO.searchByTitle(title);
    }

    /**
     * Search books by author using SQL LIKE
     */
    public List<Book> searchBooksByAuthor(String author) {
        return bookDAO.searchByAuthor(author);
    }

    /**
     * Filter books by category
     */
    public List<Book> filterBooksByCategory(String category) {
        return bookDAO.filterByCategory(category);
    }

    /**
     * Get a book by its ID
     */
    public Book getBookById(int id) {
        return bookDAO.getBookByID(id);
    }

    /**
     * Search books using regex pattern matching on title and author.
     * Falls back to simple contains search if regex is invalid.
     * 
     * @param pattern The regex pattern or simple search text
     * @return List of books matching the pattern
     */
    public List<Book> searchBooksWithRegex(String pattern) {
        List<Book> allBooks = bookDAO.getAllBooks();
        List<Book> matchingBooks = new ArrayList<>();
        
        if (pattern == null || pattern.trim().isEmpty()) {
            return allBooks;
        }
        
        Pattern regexPattern;
        boolean useRegex = true;
        
        try {
            // Try to compile as regex (case-insensitive)
            regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            // If invalid regex, fall back to simple contains search
            useRegex = false;
            regexPattern = null;
        }
        
        for (Book book : allBooks) {
            boolean matches = false;
            
            if (useRegex && regexPattern != null) {
                // Use regex matching
                if (regexPattern.matcher(book.getTitle()).find() ||
                    regexPattern.matcher(book.getAuthor()).find()) {
                    matches = true;
                }
            } else {
                // Fall back to case-insensitive contains
                String lowerPattern = pattern.toLowerCase();
                if (book.getTitle().toLowerCase().contains(lowerPattern) ||
                    book.getAuthor().toLowerCase().contains(lowerPattern)) {
                    matches = true;
                }
            }
            
            if (matches) {
                matchingBooks.add(book);
            }
        }
        
        return matchingBooks;
    }

    /**
     * Get top selling books (books with popularity > 0)
     */
    public List<Book> getTopSellingBooks(int limit) {
        return bookDAO.getTopSellingBooks(limit);
    }

    /**
     * Get books with low stock
     */
    public List<Book> getLowStockBooks(int threshold) {
        return bookDAO.getLowStockBooks(threshold);
    }
}