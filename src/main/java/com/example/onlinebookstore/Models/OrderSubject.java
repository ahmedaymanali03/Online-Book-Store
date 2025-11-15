package com.example.onlinebookstore.Models;

/**
 * Observer Pattern: Subject Interface
 * The object that observers will watch.
 */
public interface OrderSubject {
    void addObserver(OrderObserver observer);
    void removeObserver(OrderObserver observer);
    void notifyObservers(Order order);
}