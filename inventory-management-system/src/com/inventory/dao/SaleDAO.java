package com.inventory.dao;

import com.inventory.exception.InventoryException;
import com.inventory.model.Sale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SaleDAO {
    private static final Logger LOGGER = Logger.getLogger(SaleDAO.class.getName());

    public int addSale(Sale sale) {
        try (Connection conn = JDBCUtil.getConnection()) {
            return addSale(sale, conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add sale", e);
            throw new InventoryException("Failed to add sale", e);
        }
    }

    public int addSale(Sale sale, Connection conn) throws SQLException {
        String sql = "INSERT INTO sales (date, total_amount, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, sale.getDate());
            stmt.setDouble(2, sale.getTotalAmount());
            stmt.setInt(3, sale.getUserId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sales";
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                sales.add(new Sale(
                    rs.getInt("id"),
                    rs.getString("date"),
                    rs.getDouble("total_amount"),
                    rs.getInt("user_id")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch sales", e);
            throw new InventoryException("Failed to fetch sales", e);
        }
        return sales;
    }

    public List<Sale> getSalesByDateRange(String startDate, String endDate) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sales WHERE date >= ? AND date <= ? ORDER BY date";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sales.add(new Sale(
                    rs.getInt("id"),
                    rs.getString("date"),
                    rs.getDouble("total_amount"),
                    rs.getInt("user_id")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch sales by date range", e);
            throw new InventoryException("Failed to fetch sales by date range", e);
        }
        return sales;
    }
}
