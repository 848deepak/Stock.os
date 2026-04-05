package com.inventory.dao;

import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductDAO {
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, category, price, quantity, supplier_id, expiry_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setInt(5, product.getSupplierId());
            stmt.setString(6, product.getExpiryDate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add product", e);
            throw new InventoryException("Failed to add product", e);
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name=?, category=?, price=?, quantity=?, supplier_id=?, expiry_date=? WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setInt(5, product.getSupplierId());
            stmt.setString(6, product.getExpiryDate());
            stmt.setInt(7, product.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update product", e);
            throw new InventoryException("Failed to update product", e);
        }
    }

    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete product", e);
            throw new InventoryException("Failed to delete product", e);
        }
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getInt("supplier_id"),
                    rs.getString("expiry_date")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch product by id", e);
            throw new InventoryException("Failed to fetch product by id", e);
        }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getInt("supplier_id"),
                    rs.getString("expiry_date")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch products", e);
            throw new InventoryException("Failed to fetch products", e);
        }
        return products;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR category LIKE ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getInt("supplier_id"),
                    rs.getString("expiry_date")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to search products", e);
            throw new InventoryException("Failed to search products", e);
        }
        return products;
    }

    public boolean reduceStockWithValidation(Connection conn, int productId, int quantitySold) throws SQLException {
        String lockSql = "SELECT quantity FROM products WHERE id=? FOR UPDATE";
        try (PreparedStatement lockStmt = conn.prepareStatement(lockSql)) {
            lockStmt.setInt(1, productId);
            ResultSet rs = lockStmt.executeQuery();
            if (!rs.next()) {
                throw new InventoryException("Product not found: " + productId);
            }

            int currentQty = rs.getInt("quantity");
            if (quantitySold <= 0) {
                throw new InventoryException("Quantity sold must be positive");
            }
            if (currentQty < quantitySold) {
                throw new InventoryException("Insufficient stock for product " + productId + ". Available: " + currentQty + ", requested: " + quantitySold);
            }
        }

        String updateSql = "UPDATE products SET quantity = quantity - ? WHERE id=?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setInt(1, quantitySold);
            updateStmt.setInt(2, productId);
            return updateStmt.executeUpdate() == 1;
        }
    }
}
