package com.example.onlinebookstore.Models;

import java.util.Comparator;
import java.util.List;

/**
 * Concrete Strategy: Sorts by popularity, high to low.
 */
public class SortByPopularity implements SortStrategy {
    @Override
    public void sort(List<Book> books) {
        books.sort(Comparator.comparingInt(Book::getPopularity).reversed());
    }
}