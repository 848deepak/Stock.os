package com.inventory.view;

import com.inventory.model.Product;
import com.inventory.model.Sale;
import com.inventory.model.SaleItem;
import com.inventory.service.ProductService;
import com.inventory.service.SalesService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SalesPanel extends JPanel {
    private ProductService productService = new ProductService();
    private SalesService salesService = new SalesService();
    private JTable salesTable;
    private DefaultTableModel salesTableModel;
    private JButton createSaleButton;

    public SalesPanel() {
        setLayout(new BorderLayout());
        salesTableModel = new DefaultTableModel(new Object[]{"Sale ID", "Date", "Total Amount", "User ID"}, 0);
        salesTable = new JTable(salesTableModel);
        refreshSalesTable();

        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        createSaleButton = new JButton("Create Sale / Invoice");
        add(createSaleButton, BorderLayout.NORTH);

        createSaleButton.addActionListener(e -> createSaleDialog());
    }

    private void refreshSalesTable() {
        salesTableModel.setRowCount(0);
        List<Sale> sales = salesService.getAllSales();
        for (Sale s : sales) {
            salesTableModel.addRow(new Object[]{s.getId(), s.getDate(), s.getTotalAmount(), s.getUserId()});
        }
    }

    private void createSaleDialog() {
        List<Product> products = productService.getAllProducts();
        JPanel panel = new JPanel(new GridLayout(products.size() + 2, 4));
        List<JTextField> qtyFields = new ArrayList<>();
        panel.add(new JLabel("Product"));
        panel.add(new JLabel("Price"));
        panel.add(new JLabel("Available"));
        panel.add(new JLabel("Quantity"));
        for (Product p : products) {
            panel.add(new JLabel(p.getName()));
            panel.add(new JLabel(String.valueOf(p.getPrice())));
            panel.add(new JLabel(String.valueOf(p.getQuantity())));
            JTextField qtyField = new JTextField("0");
            qtyFields.add(qtyField);
            panel.add(qtyField);
        }
        JTextField userIdField = new JTextField();
        panel.add(new JLabel("User ID:"));
        panel.add(userIdField);
        panel.add(new JLabel());
        panel.add(new JLabel());
        int option = JOptionPane.showConfirmDialog(this, panel, "Create Sale / Invoice", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                List<SaleItem> items = new ArrayList<>();
                double total = 0;
                for (int i = 0; i < products.size(); i++) {
                    int qty = Integer.parseInt(qtyFields.get(i).getText().trim());
                    if (qty < 0) {
                        JOptionPane.showMessageDialog(this, "Quantity cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (qty > 0) {
                        Product p = products.get(i);
                        if (qty > p.getQuantity()) {
                            JOptionPane.showMessageDialog(this, "Requested quantity exceeds available stock for " + p.getName(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        items.add(new SaleItem(0, 0, p.getId(), qty, p.getPrice()));
                        total += p.getPrice() * qty;
                    }
                }

                if (items.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Add at least one item with quantity greater than zero.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int userId = Integer.parseInt(userIdField.getText().trim());
                if (userId <= 0) {
                    JOptionPane.showMessageDialog(this, "User ID must be positive.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String date = java.time.LocalDateTime.now().toString();
                Sale sale = new Sale(0, date, total, userId);
                salesService.createSale(sale, items);
                refreshSalesTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantities and User ID must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
