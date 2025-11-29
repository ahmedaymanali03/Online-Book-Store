package com.example.onlinebookstore.Models;

import java.util.List;

/**
 * Concrete Strategy: Sorts by title alphabetically (A to Z).
 */
public class SortByTitleAZ implements SortStrategy {
    @Override
    public void sort(List<Book> books) {
        books.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
    }
}
