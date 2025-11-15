package com.example.onlinebookstore.Models;
/**
 * Book Model
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private double price;
    private int stock;
    private String category;
    private int popularity;
    private String edition;
    private String coverImage;

    // Full constructor
    public Book(int id, String title, String author, double price, int stock, String category, int popularity, String edition, String coverImage) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.popularity = popularity;
        this.edition = edition;
        this.coverImage = coverImage;
    }
    
    // Constructor without edition and coverImage for backward compatibility
    public Book(int id, String title, String author, double price, int stock, String category, int popularity) {
        this(id, title, author, price, stock, category, popularity, null, null);
    }

    // Getters and Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getPopularity() { return popularity; }
    public void setPopularity(int popularity) { this.popularity = popularity; }
    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}