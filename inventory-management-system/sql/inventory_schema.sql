-- Inventory Management System Database Schema
-- Author: Deepak Pandey
-- Date: 2026-03-18

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Staff') NOT NULL
);

CREATE TABLE suppliers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    address VARCHAR(255)
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    supplier_id INT,
    expiry_date DATE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE sales_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT,
    product_id INT,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Performance indexes for common production queries
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_supplier_id ON products(supplier_id);
CREATE INDEX idx_sales_date ON sales(date);
CREATE INDEX idx_sales_user_id ON sales(user_id);
CREATE INDEX idx_sales_items_sale_id ON sales_items(sale_id);
CREATE INDEX idx_sales_items_product_id ON sales_items(product_id);

-- Sample Queries
-- Add a user
-- INSERT INTO users (username, password, role) VALUES ('admin', 'hashed_password', 'Admin');

-- Add a supplier
-- INSERT INTO suppliers (name, contact, address) VALUES ('ABC Traders', '9876543210', 'Main Street');

-- Add a product
-- INSERT INTO products (name, category, price, quantity, supplier_id) VALUES ('Milk', 'Dairy', 50.00, 100, 1);

-- Record a sale
-- INSERT INTO sales (date, total_amount, user_id) VALUES (NOW(), 500.00, 1);

-- Add sales items
-- INSERT INTO sales_items (sale_id, product_id, quantity, price) VALUES (1, 1, 2, 50.00);