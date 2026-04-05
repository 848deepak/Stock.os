package com.inventory.dao;

import com.inventory.exception.InventoryException;
import com.inventory.model.SaleItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaleItemDAO {
    private static final Logger LOGGER = Logger.getLogger(SaleItemDAO.class.getName());

    public boolean addSaleItem(SaleItem item) {
        try (Connection conn = JDBCUtil.getConnection()) {
            return addSaleItem(item, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add sale item", e);
            throw new InventoryException("Failed to add sale item", e);
        }
    }

    public boolean addSaleItem(SaleItem item, Connection conn) throws SQLException {
        String sql = "INSERT INTO sales_items (sale_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getSaleId());
            stmt.setInt(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<SaleItem> getSaleItemsBySaleId(int saleId) {
        List<SaleItem> items = new ArrayList<>();
        String sql = "SELECT * FROM sales_items WHERE sale_id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, saleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new SaleItem(
                    rs.getInt("id"),
                    rs.getInt("sale_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch sale items by sale id", e);
            throw new InventoryException("Failed to fetch sale items by sale id", e);
        }
        return items;
    }

    public List<SaleItem> getSaleItemsByProductId(int productId) {
        List<SaleItem> items = new ArrayList<>();
        String sql = "SELECT * FROM sales_items WHERE product_id=? ORDER BY id";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new SaleItem(
                    rs.getInt("id"),
                    rs.getInt("sale_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch sale items by product id", e);
            throw new InventoryException("Failed to fetch sale items by product id", e);
        }
        return items;
    }

    public int getTotalSoldQuantityByProductId(int productId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) AS total_qty FROM sales_items WHERE product_id=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_qty");
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch total sold quantity", e);
            throw new InventoryException("Failed to fetch total sold quantity", e);
        }
    }

    public Map<Integer, Integer> getProductSalesCount() {
        Map<Integer, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT product_id, COALESCE(SUM(quantity), 0) AS sold_qty FROM sales_items GROUP BY product_id ORDER BY sold_qty DESC";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("product_id"), rs.getInt("sold_qty"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch product sales count", e);
            throw new InventoryException("Failed to fetch product sales count", e);
        }
        return result;
    }
}
