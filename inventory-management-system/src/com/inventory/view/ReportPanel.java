package com.inventory.view;

import com.inventory.model.Product;
import com.inventory.model.Sale;
import com.inventory.service.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ReportPanel extends JPanel {
    private ReportService reportService = new ReportService();
    private JTable salesTable;
    private JTable mostSoldTable;
    private JTable lowStockTable;
    private DefaultTableModel salesTableModel;
    private DefaultTableModel mostSoldTableModel;
    private DefaultTableModel lowStockTableModel;

    public ReportPanel() {
        setLayout(new GridLayout(3, 1));

        // Sales Report
        salesTableModel = new DefaultTableModel(new Object[]{"Sale ID", "Date", "Total Amount", "User ID"}, 0);
        salesTable = new JTable(salesTableModel);
        refreshSalesTable();
        add(new JScrollPane(salesTable));

        // Most Sold Products
        mostSoldTableModel = new DefaultTableModel(new Object[]{"Product ID", "Sold Quantity"}, 0);
        mostSoldTable = new JTable(mostSoldTableModel);
        refreshMostSoldTable();
        add(new JScrollPane(mostSoldTable));

        // Low Stock Report
        lowStockTableModel = new DefaultTableModel(new Object[]{"Product ID", "Name", "Quantity"}, 0);
        lowStockTable = new JTable(lowStockTableModel);
        refreshLowStockTable();
        add(new JScrollPane(lowStockTable));
    }

    private void refreshSalesTable() {
        salesTableModel.setRowCount(0);
        String start = java.time.LocalDate.now().minusYears(10).toString() + "T00:00:00";
        String end = java.time.LocalDate.now().plusDays(1).toString() + "T00:00:00";
        List<Sale> sales = reportService.getSalesByDateRange(start, end);
        for (Sale s : sales) {
            salesTableModel.addRow(new Object[]{s.getId(), s.getDate(), s.getTotalAmount(), s.getUserId()});
        }
    }

    private void refreshMostSoldTable() {
        mostSoldTableModel.setRowCount(0);
        Map<Integer, Integer> productSales = reportService.getProductSalesCount();
        for (Map.Entry<Integer, Integer> entry : productSales.entrySet()) {
            mostSoldTableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }

    private void refreshLowStockTable() {
        lowStockTableModel.setRowCount(0);
        List<Product> lowStock = reportService.getLowStockProducts();
        for (Product p : lowStock) {
            lowStockTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getQuantity()});
        }
    }
}
