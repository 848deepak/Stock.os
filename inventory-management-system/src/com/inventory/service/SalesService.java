package com.inventory.service;

import com.inventory.dao.JDBCUtil;
import com.inventory.dao.SaleDAO;
import com.inventory.dao.SaleItemDAO;
import com.inventory.exception.InventoryException;
import com.inventory.model.Sale;
import com.inventory.model.SaleItem;
import com.inventory.util.ValidationUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SalesService {
    private final SaleDAO saleDAO;
    private final SaleItemDAO saleItemDAO;
    private final StockService stockService;

    public SalesService() {
        this(new SaleDAO(), new SaleItemDAO(), new StockService());
    }

    public SalesService(SaleDAO saleDAO, SaleItemDAO saleItemDAO, StockService stockService) {
        this.saleDAO = saleDAO;
        this.saleItemDAO = saleItemDAO;
        this.stockService = stockService;
    }

    public int createSale(Sale sale, List<SaleItem> items) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Sale items cannot be empty");
        }
        ValidationUtil.requireNonNegative(sale.getTotalAmount(), "Total amount");
        ValidationUtil.requirePositive(sale.getUserId(), "User ID");
        ValidationUtil.requireNonBlank(sale.getDate(), "Sale date");

        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            conn.setAutoCommit(false);
            int saleId = saleDAO.addSale(sale, conn);
            if (saleId <= 0) {
                conn.rollback();
                throw new InventoryException("Failed to create sale");
            }

            for (SaleItem item : items) {
                ValidationUtil.requirePositive(item.getProductId(), "Product ID");
                ValidationUtil.requirePositive(item.getQuantity(), "Quantity");
                ValidationUtil.requireNonNegative(item.getPrice(), "Price");

                item.setSaleId(saleId);
                boolean itemSaved = saleItemDAO.addSaleItem(item, conn);
                if (!itemSaved) {
                    conn.rollback();
                    throw new InventoryException("Failed to save sale item");
                }
                stockService.reduceStockInTransaction(conn, item.getProductId(), item.getQuantity());
            }

            conn.commit();
            return saleId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new InventoryException("Sale transaction rollback failed", rollbackEx);
                }
            }
            throw new InventoryException("Sale transaction failed", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                    // Closing failure should not shadow original exception.
                }
            }
        }
    }

    public List<Sale> getAllSales() {
        return saleDAO.getAllSales();
    }

    public List<SaleItem> getSaleItemsBySaleId(int saleId) {
        return saleItemDAO.getSaleItemsBySaleId(saleId);
    }
}
