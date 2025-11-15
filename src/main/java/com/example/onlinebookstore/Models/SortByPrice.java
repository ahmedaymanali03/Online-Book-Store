package com.example.onlinebookstore.Models;


import java.util.Comparator;
import java.util.List;

/**
 * Concrete Strategy: Sorts by price, low to high.
 */
public class SortByPrice implements SortStrategy {
    @Override
    public void sort(List<Book> books) {
        books.sort(Comparator.comparingDouble(Book::getPrice));
    }
}