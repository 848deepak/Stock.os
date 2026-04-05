package com.inventory.service;

import com.inventory.dao.ProductDAO;
import com.inventory.model.Product;
import com.inventory.util.ValidationUtil;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() {
        this(new ProductDAO());
    }

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public boolean addProduct(Product product) {
        validateProduct(product);
        return productDAO.addProduct(product);
    }

    public boolean updateProduct(Product product) {
        validateProduct(product);
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(int id) {
        return productDAO.deleteProduct(id);
    }

    public Product getProductById(int id) {
        return productDAO.getProductById(id);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> searchProducts(String keyword) {
        return productDAO.searchProducts(keyword);
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        ValidationUtil.requireNonBlank(product.getName(), "Product name");
        ValidationUtil.requireNonNegative(product.getPrice(), "Price");
        ValidationUtil.requireNonNegative(product.getQuantity(), "Quantity");
        ValidationUtil.requireNonNegative(product.getSupplierId(), "Supplier ID");
        ValidationUtil.validateIsoDateOrBlank(product.getExpiryDate(), "Expiry date");
    }
}
