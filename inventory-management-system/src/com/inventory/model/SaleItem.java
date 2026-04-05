package com.inventory.model;

public class SaleItem {
    private int id;
    private int saleId;
    private int productId;
    private int quantity;
    private double price;

    public SaleItem(int id, int saleId, int productId, int quantity, double price) {
        this.id = id;
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and setters
    public int getId() { return id; }
    public int getSaleId() { return saleId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    public void setId(int id) { this.id = id; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
}
