package com.example.onlinebookstore.Models;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {
    
    // Egyptian phone number: 11 digits, starts with 01, third digit is 0, 1, 2, or 5
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[0125]\\d{8}$");
    
    // Username: alphanumeric, 3-20 characters
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    // Password: at least 6 characters
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");
    
    // Price: positive number with optional 2 decimal places
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    
    // Stock: positive integer
    private static final Pattern STOCK_PATTERN = Pattern.compile("^\\d+$");
    
    /**
     * Validate Egyptian phone number
     * Format: 11 digits starting with 01, third digit is 0, 1, 2, or 5
     * Examples: 01012345678, 01112345678, 01212345678, 01512345678
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Get phone validation error message
     */
    public static String getPhoneErrorMessage() {
        return "Phone must be 11 digits starting with 01 followed by 0, 1, 2, or 5\n(e.g., 01012345678)";
    }
    
    /**
     * Validate username
     * Must be 3-20 alphanumeric characters or underscores
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Get username validation error message
     */
    public static String getUsernameErrorMessage() {
        return "Username must be 3-20 characters (letters, numbers, underscores only)";
    }
    
    /**
     * Validate password
     * Must be at least 6 characters
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Get password validation error message
     */
    public static String getPasswordErrorMessage() {
        return "Password must be at least 6 characters";
    }
    
    /**
     * Validate price
     * Must be a positive number with up to 2 decimal places
     */
    public static boolean isValidPrice(String price) {
        if (price == null || price.trim().isEmpty()) {
            return false;
        }
        if (!PRICE_PATTERN.matcher(price.trim()).matches()) {
            return false;
        }
        try {
            double value = Double.parseDouble(price.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Get price validation error message
     */
    public static String getPriceErrorMessage() {
        return "Price must be a positive number (e.g., 19.99)";
    }
    
    /**
     * Validate stock quantity
     * Must be a non-negative integer
     */
    public static boolean isValidStock(String stock) {
        if (stock == null || stock.trim().isEmpty()) {
            return false;
        }
        if (!STOCK_PATTERN.matcher(stock.trim()).matches()) {
            return false;
        }
        try {
            int value = Integer.parseInt(stock.trim());
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Get stock validation error message
     */
    public static String getStockErrorMessage() {
        return "Stock must be a non-negative whole number";
    }
    
    /**
     * Check if a string is not empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Get not empty error message for a field
     */
    public static String getNotEmptyErrorMessage(String fieldName) {
        return fieldName + " cannot be empty";
    }
}
