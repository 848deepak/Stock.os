package com.inventory.view;

import com.inventory.model.Product;
import com.inventory.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel {
    private ProductService productService = new ProductService();
    private JTable productTable;
    private DefaultTableModel tableModel;

    public ProductPanel() {
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Price", "Quantity", "Supplier", "Expiry"}, 0);
        productTable = new JTable(tableModel);
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton addButton = new JButton("Add Product");
        JButton updateButton = new JButton("Update Product");
        JButton deleteButton = new JButton("Delete Product");
        JButton searchButton = new JButton("Search");
        JTextField searchField = new JTextField(15);

        controlPanel.add(addButton);
        controlPanel.add(updateButton);
        controlPanel.add(deleteButton);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        add(controlPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> addProductDialog());
        updateButton.addActionListener(e -> updateProductDialog());
        deleteButton.addActionListener(e -> deleteProduct());
        searchButton.addActionListener(e -> searchProducts(searchField.getText()));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Product> products = productService.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity(), p.getSupplierId(), p.getExpiryDate()});
        }
    }

    private void addProductDialog() {
        // Simple dialog for adding product
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField supplierField = new JTextField();
        JTextField expiryField = new JTextField();
        Object[] fields = {
                "Name:", nameField,
                "Category:", categoryField,
                "Price:", priceField,
                "Quantity:", quantityField,
                "Supplier ID:", supplierField,
                "Expiry Date (YYYY-MM-DD):", expiryField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Product name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                int supplierId = Integer.parseInt(supplierField.getText().trim());

                if (price < 0 || quantity < 0 || supplierId < 0) {
                    JOptionPane.showMessageDialog(this, "Price, quantity, and supplier ID cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Product p = new Product(0, name, categoryField.getText().trim(), price, quantity, supplierId, expiryField.getText().trim());
                productService.addProduct(p);
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price, quantity, and supplier ID must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateProductDialog() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to update.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Product p = productService.getProductById(id);
        JTextField nameField = new JTextField(p.getName());
        JTextField categoryField = new JTextField(p.getCategory());
        JTextField priceField = new JTextField(String.valueOf(p.getPrice()));
        JTextField quantityField = new JTextField(String.valueOf(p.getQuantity()));
        JTextField supplierField = new JTextField(String.valueOf(p.getSupplierId()));
        JTextField expiryField = new JTextField(p.getExpiryDate());
        Object[] fields = {
                "Name:", nameField,
                "Category:", categoryField,
                "Price:", priceField,
                "Quantity:", quantityField,
                "Supplier ID:", supplierField,
                "Expiry Date (YYYY-MM-DD):", expiryField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Update Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Product name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                int supplierId = Integer.parseInt(supplierField.getText().trim());

                if (price < 0 || quantity < 0 || supplierId < 0) {
                    JOptionPane.showMessageDialog(this, "Price, quantity, and supplier ID cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Product updated = new Product(id, name, categoryField.getText().trim(), price, quantity, supplierId, expiryField.getText().trim());
                productService.updateProduct(updated);
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price, quantity, and supplier ID must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        productService.deleteProduct(id);
        refreshTable();
    }

    private void searchProducts(String keyword) {
        tableModel.setRowCount(0);
        List<Product> products = productService.searchProducts(keyword);
        for (Product p : products) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity(), p.getSupplierId(), p.getExpiryDate()});
        }
    }
}
