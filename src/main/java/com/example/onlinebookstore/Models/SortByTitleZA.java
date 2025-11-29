package com.example.onlinebookstore.Models;

import java.util.List;

/**
 * Concrete Strategy: Sorts by title reverse alphabetically (Z to A).
 */
public class SortByTitleZA implements SortStrategy {
    @Override
    public void sort(List<Book> books) {
        books.sort((b1, b2) -> b2.getTitle().compareToIgnoreCase(b1.getTitle()));
    }
}
