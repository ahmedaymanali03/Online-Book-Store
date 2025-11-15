package com.example.onlinebookstore.Models;

/**
 * Service for user logic.
 * Uses the UserDAO.
 */
public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.password.equals(password)) { // BAD: Use hashing
            return user;
        }
        return null; // Login failed
    }

    public void registerCustomer(String username, String password, String address, String phone) {
        Customer customer = new Customer(username, password, address, phone);
        userDAO.createUser(customer);
    }
}