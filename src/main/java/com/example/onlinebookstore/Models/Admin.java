package com.example.onlinebookstore.Models;

/**
 * Admin specialization of User
 */
public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password, "ADMIN");
    }

    // Admin-specific methods can go here
}