package com.example.onlinebookstore.Models;

/**
 * Order Model
 */
import java.util.List;

public class Order {
    private int id;
    private int customerId;
    private String orderDate;
    private String status; // 'PENDING', 'CONFIRMED', etc.
    private double totalPrice;
    private List<OrderItem> orderItems;

    // Constructor, Getters, Setters...
    public Order(int customerId, String orderDate, String status, double totalPrice) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
}
