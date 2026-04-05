package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.service.StockService;
import java.util.List;

public class NotificationService {
    private final StockService stockService;

    public NotificationService() {
        this(new StockService());
    }

    public NotificationService(StockService stockService) {
        this.stockService = stockService;
    }

    public String getLowStockAlerts() {
        List<Product> lowStock = stockService.getLowStockProducts();
        if (lowStock.isEmpty()) return "All stocks are sufficient.";
        StringBuilder sb = new StringBuilder();
        for (Product p : lowStock) {
            sb.append("Low stock: ").append(p.getName()).append(" (Qty: ").append(p.getQuantity()).append(")\n");
        }
        return sb.toString();
    }

    public String getOutOfStockWarnings() {
        List<Product> lowStock = stockService.getLowStockProducts();
        StringBuilder sb = new StringBuilder();
        for (Product p : lowStock) {
            if (p.getQuantity() == 0) {
                sb.append("Out of stock: ").append(p.getName()).append("\n");
            }
        }
        return sb.length() == 0 ? "No out-of-stock products." : sb.toString();
    }
}
