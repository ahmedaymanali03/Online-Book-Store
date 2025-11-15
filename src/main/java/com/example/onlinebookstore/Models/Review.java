package com.example.onlinebookstore.Models;

/**
 * Review Model
 */
public class Review {
    private int id;
    private int bookId;
    private int customerId;
    private int rating; // 1-5 stars
    private String comment;
    private String reviewDate;

    public Review(int bookId, int customerId, int rating, String comment, String reviewDate) {
        this.bookId = bookId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getReviewDate() { return reviewDate; }
    public void setReviewDate(String reviewDate) { this.reviewDate = reviewDate; }
}
