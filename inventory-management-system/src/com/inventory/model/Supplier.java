package com.inventory.model;

public class Supplier {
    private int id;
    private String name;
    private String contact;
    private String address;

    public Supplier(int id, String name, String contact, String address) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.address = address;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getAddress() { return address; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setAddress(String address) { this.address = address; }
}
