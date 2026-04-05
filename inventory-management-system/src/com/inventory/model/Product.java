package com.inventory.model;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int supplierId;
    private String expiryDate;

    public Product(int id, String name, String category, double price, int quantity, int supplierId, String expiryDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.expiryDate = expiryDate;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getSupplierId() { return supplierId; }
    public String getExpiryDate() { return expiryDate; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
