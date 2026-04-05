package com.inventory.service;

import com.inventory.dao.SaleItemDAO;
import com.inventory.model.SaleItem;
import java.util.List;

public class SmartFeatureService {
    private final SaleItemDAO saleItemDAO;

    public SmartFeatureService() {
        this(new SaleItemDAO());
    }

    public SmartFeatureService(SaleItemDAO saleItemDAO) {
        this.saleItemDAO = saleItemDAO;
    }

    // Inventory prediction using moving average
    public double predictNextInventory(int productId, int window) {
        if (window <= 0) {
            throw new IllegalArgumentException("Window must be greater than zero");
        }

        List<SaleItem> allItems = saleItemDAO.getSaleItemsByProductId(productId);
        int n = allItems.size();
        if (n < window) {
            return 0;
        }
        double sum = 0;
        for (int i = n - window; i < n; i++) {
            sum += allItems.get(i).getQuantity();
        }
        return sum / window;
    }

    // Auto reorder suggestion
    public boolean shouldReorder(int productId, int threshold) {
        // If predicted demand is above threshold, suggest reorder
        double predicted = predictNextInventory(productId, 5);
        return predicted > threshold;
    }

    // Demand trend tracking (simple: total sales per product)
    public int getTotalSalesForProduct(int productId) {
        return saleItemDAO.getTotalSoldQuantityByProductId(productId);
    }
}
