package com.inventory.dao;

import com.inventory.exception.InventoryException;
import com.inventory.model.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SupplierDAO {
    private static final Logger LOGGER = Logger.getLogger(SupplierDAO.class.getName());

    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact, address) VALUES (?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContact());
            stmt.setString(3, supplier.getAddress());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add supplier", e);
            throw new InventoryException("Failed to add supplier", e);
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET name=?, contact=?, address=? WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContact());
            stmt.setString(3, supplier.getAddress());
            stmt.setInt(4, supplier.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to update supplier", e);
            throw new InventoryException("Failed to update supplier", e);
        }
    }

    public boolean deleteSupplier(int id) {
        String sql = "DELETE FROM suppliers WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete supplier", e);
            throw new InventoryException("Failed to delete supplier", e);
        }
    }

    public Supplier getSupplierById(int id) {
        String sql = "SELECT * FROM suppliers WHERE id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Supplier(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("address")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch supplier by id", e);
            throw new InventoryException("Failed to fetch supplier by id", e);
        }
        return null;
    }

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                suppliers.add(new Supplier(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch suppliers", e);
            throw new InventoryException("Failed to fetch suppliers", e);
        }
        return suppliers;
    }
}
