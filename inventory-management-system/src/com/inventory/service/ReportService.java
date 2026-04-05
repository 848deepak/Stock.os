package com.inventory.service;

import com.inventory.dao.SaleDAO;
import com.inventory.dao.SaleItemDAO;
import com.inventory.model.Sale;
import com.inventory.model.Product;
import com.inventory.dao.ProductDAO;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final SaleDAO saleDAO;
    private final SaleItemDAO saleItemDAO;
    private final ProductDAO productDAO;

    public ReportService() {
        this(new SaleDAO(), new SaleItemDAO(), new ProductDAO());
    }

    public ReportService(SaleDAO saleDAO, SaleItemDAO saleItemDAO, ProductDAO productDAO) {
        this.saleDAO = saleDAO;
        this.saleItemDAO = saleItemDAO;
        this.productDAO = productDAO;
    }

    public List<Sale> getSalesByDateRange(String start, String end) {
        return saleDAO.getSalesByDateRange(start, end);
    }

    public Map<Integer, Integer> getProductSalesCount() {
        return saleItemDAO.getProductSalesCount();
    }

    public List<Product> getLowStockProducts() {
        List<Product> products = productDAO.getAllProducts();
        return products.stream().filter(p -> p.getQuantity() <= 10).toList();
    }
}
