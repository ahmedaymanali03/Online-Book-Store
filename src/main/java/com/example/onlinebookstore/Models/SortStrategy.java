package com.example.onlinebookstore.Models;

import java.util.List;

/**
 * Strategy Pattern Interface:
 * Defines the contract for all sorting strategies.
 */
public interface SortStrategy {
    void sort(List<Book> books);
}