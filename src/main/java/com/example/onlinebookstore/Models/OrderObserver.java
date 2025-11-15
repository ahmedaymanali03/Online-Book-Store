package com.example.onlinebookstore.Models;

public interface OrderObserver {
    void onOrderConfirmed(Order order);
}
