package com.inventory.service;

import com.inventory.dao.SupplierDAO;
import com.inventory.model.Supplier;
import com.inventory.util.ValidationUtil;
import java.util.List;

public class SupplierService {
    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this(new SupplierDAO());
    }

    public SupplierService(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    public boolean addSupplier(Supplier supplier) {
        validateSupplier(supplier);
        return supplierDAO.addSupplier(supplier);
    }

    public boolean updateSupplier(Supplier supplier) {
        validateSupplier(supplier);
        return supplierDAO.updateSupplier(supplier);
    }

    public boolean deleteSupplier(int id) {
        return supplierDAO.deleteSupplier(id);
    }

    public Supplier getSupplierById(int id) {
        return supplierDAO.getSupplierById(id);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierDAO.getAllSuppliers();
    }

    private void validateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        ValidationUtil.requireNonBlank(supplier.getName(), "Supplier name");
    }
}
