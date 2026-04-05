package com.inventory.view;

import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierPanel extends JPanel {
    private SupplierService supplierService = new SupplierService();
    private JTable supplierTable;
    private DefaultTableModel tableModel;

    public SupplierPanel() {
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact", "Address"}, 0);
        supplierTable = new JTable(tableModel);
        refreshTable();

        JScrollPane scrollPane = new JScrollPane(supplierTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton addButton = new JButton("Add Supplier");
        JButton updateButton = new JButton("Update Supplier");
        JButton deleteButton = new JButton("Delete Supplier");

        controlPanel.add(addButton);
        controlPanel.add(updateButton);
        controlPanel.add(deleteButton);
        add(controlPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> addSupplierDialog());
        updateButton.addActionListener(e -> updateSupplierDialog());
        deleteButton.addActionListener(e -> deleteSupplier());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        for (Supplier s : suppliers) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getContact(), s.getAddress()});
        }
    }

    private void addSupplierDialog() {
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
        Object[] fields = {
                "Name:", nameField,
                "Contact:", contactField,
                "Address:", addressField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Add Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Supplier name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Supplier s = new Supplier(0, name, contactField.getText().trim(), addressField.getText().trim());
                supplierService.addSupplier(s);
                refreshTable();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateSupplierDialog() {
        int row = supplierTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a supplier to update.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Supplier s = supplierService.getSupplierById(id);
        JTextField nameField = new JTextField(s.getName());
        JTextField contactField = new JTextField(s.getContact());
        JTextField addressField = new JTextField(s.getAddress());
        Object[] fields = {
                "Name:", nameField,
                "Contact:", contactField,
                "Address:", addressField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Update Supplier", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Supplier name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Supplier updated = new Supplier(id, name, contactField.getText().trim(), addressField.getText().trim());
                supplierService.updateSupplier(updated);
                refreshTable();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a supplier to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        supplierService.deleteSupplier(id);
        refreshTable();
    }
}
