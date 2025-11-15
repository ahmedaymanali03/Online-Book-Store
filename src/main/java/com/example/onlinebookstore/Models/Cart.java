package com.example.onlinebookstore.Models;


import java.util.HashMap;
import java.util.Map;

public class Cart {
    // Map of Book ID to Quantity
    private Map<Integer, Integer> items = new HashMap<>();

    public void addBook(Book book, int quantity) {
        items.put(book.getId(), items.getOrDefault(book.getId(), 0) + quantity);
    }

    public void removeBook(Book book) {
        items.remove(book.getId());
    }

    public void updateQuantity(Book book, int quantity) {
        if (quantity <= 0) {
            removeBook(book);
        } else {
            items.put(book.getId(), quantity);
        }
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }
}