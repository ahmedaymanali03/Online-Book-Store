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
    private int categoryId;  // Foreign key to Category
    private Category category; // Category object for OOP
    private int popularity;
    private String edition;
    private String coverImage;

    // Full constructor with Category object
    public Book(int id, String title, String author, double price, int stock, Category category, int popularity, String edition, String coverImage) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.categoryId = category != null ? category.getId() : 0;
        this.popularity = popularity;
        this.edition = edition;
        this.coverImage = coverImage;
    }

    // Constructor with categoryId (for database loading)
    public Book(int id, String title, String author, double price, int stock, int categoryId, int popularity, String edition, String coverImage) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
        this.category = null;
        this.popularity = popularity;
        this.edition = edition;
        this.coverImage = coverImage;
    }

    // Constructor with category name string (backward compatibility)
    public Book(int id, String title, String author, double price, int stock, String categoryName, int popularity, String edition, String coverImage) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
        this.category = new Category(categoryName, "");
        this.categoryId = 0;
        this.popularity = popularity;
        this.edition = edition;
        this.coverImage = coverImage;
    }

    // Simple constructor for backward compatibility
    public Book(int id, String title, String author, double price, int stock, String categoryName, int popularity) {
        this(id, title, author, price, stock, categoryName, popularity, null, null);
    }

    // Getters and Setters
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
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public Category getCategoryObject() { return category; }
    public void setCategory(Category category) { 
        this.category = category;
        this.categoryId = category != null ? category.getId() : 0;
    }
    
    // Returns category name as String (for backward compatibility)
    public String getCategory() { 
        return category != null ? category.getName() : null; 
    }
    
    public int getPopularity() { return popularity; }
    public void setPopularity(int popularity) { this.popularity = popularity; }
    
    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}