package com.example.onlinebookstore.Models;

import java.util.List;

/**
 * Concrete Strategy: Sorts by price, high to low.
 */
public class SortByPriceHighToLow implements SortStrategy {
    @Override
    public void sort(List<Book> books) {
        books.sort((b1, b2) -> Double.compare(b2.getPrice(), b1.getPrice()));
    }
}
