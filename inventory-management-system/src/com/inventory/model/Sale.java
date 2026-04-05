package com.inventory.model;

public class Sale {
    private int id;
    private String date;
    private double totalAmount;
    private int userId;

    public Sale(int id, String date, double totalAmount, int userId) {
        this.id = id;
        this.date = date;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getDate() { return date; }
    public double getTotalAmount() { return totalAmount; }
    public int getUserId() { return userId; }

    public void setId(int id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setUserId(int userId) { this.userId = userId; }
}
