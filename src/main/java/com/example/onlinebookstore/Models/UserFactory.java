package com.example.onlinebookstore.Models;

public class UserFactory {
    public static User createUser(String role, String username, String password, String address, String phone) {
        if (role.equalsIgnoreCase("ADMIN")) {
            return new Admin(username, password);
        } else if (role.equalsIgnoreCase("CUSTOMER")) {
            return new Customer(username, password, address, phone);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
