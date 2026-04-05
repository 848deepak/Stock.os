package com.inventory.service;

import com.inventory.dao.ProductDAO;
import com.inventory.exception.InventoryException;
import com.inventory.model.Product;
import com.inventory.util.ValidationUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StockService {
    private final ProductDAO productDAO;
    private static final int LOW_STOCK_THRESHOLD = 10;

    public StockService() {
        this(new ProductDAO());
    }

    public StockService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> getLowStockProducts() {
        List<Product> allProducts = productDAO.getAllProducts();
        return allProducts.stream()
                .filter(p -> p.getQuantity() <= LOW_STOCK_THRESHOLD)
                .toList();
    }

    public void reduceStock(int productId, int quantitySold) {
        ValidationUtil.requirePositive(quantitySold, "Quantity sold");
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            throw new InventoryException("Product not found: " + productId);
        }
        if (product.getQuantity() < quantitySold) {
            throw new InventoryException("Insufficient stock for product " + productId);
        }
        product.setQuantity(product.getQuantity() - quantitySold);
        productDAO.updateProduct(product);
    }

    public void reduceStockInTransaction(Connection conn, int productId, int quantitySold) throws SQLException {
        ValidationUtil.requirePositive(quantitySold, "Quantity sold");
        if (!productDAO.reduceStockWithValidation(conn, productId, quantitySold)) {
            throw new InventoryException("Failed to reduce stock for product " + productId);
        }
    }

    public boolean isLowStock(int productId) {
        Product product = productDAO.getProductById(productId);
        return product != null && product.getQuantity() <= LOW_STOCK_THRESHOLD;
    }
}
