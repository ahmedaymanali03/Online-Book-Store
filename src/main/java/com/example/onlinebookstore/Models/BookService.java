package com.example.onlinebookstore.Models;

import java.util.List;

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

    // Other methods: searchBooks, filterByCategory, getBookDetails
}