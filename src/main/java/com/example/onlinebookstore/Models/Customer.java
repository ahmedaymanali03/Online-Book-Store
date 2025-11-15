package com.example.onlinebookstore.Models;

/**
 * Customer specialization of User
 */
public class Customer extends User {
    private String address;
    private String phone;
    private Cart cart;

    public Customer(String username, String password, String address, String phone) {
        super(username, password, "CUSTOMER");
        this.address = address;
        this.phone = phone;
        this.cart = new Cart();
    }

    // Getters, Setters for address, phone, cart
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
}