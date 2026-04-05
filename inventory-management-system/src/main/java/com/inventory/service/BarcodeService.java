package com.inventory.service;

import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.util.BarcodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for barcode generation and management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BarcodeService {
    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;

    public String generateBarcodeForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        String barcodeValue = BarcodeGenerator.generateUniqueBarcode(product.getSku(), productId);
        product.setBarcode(barcodeValue);
        productRepository.save(product);

        auditLogService.log("Product", productId, "UPDATE", null, "Barcode generated");
        return barcodeValue;
    }

    public String getBarcodeImage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getBarcode() == null) {
            generateBarcodeForProduct(productId);
        }

        return BarcodeGenerator.generateBarcode(product.getBarcode());
    }

    public Product getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found by barcode: " + barcode));
    }

    public String regenerateBarcode(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        String oldBarcode = product.getBarcode();
        String newBarcode = BarcodeGenerator.generateUniqueBarcode(product.getSku(), productId + System.currentTimeMillis());
        product.setBarcode(newBarcode);
        productRepository.save(product);

        auditLogService.log("Product", productId, "UPDATE", oldBarcode, newBarcode);
        return newBarcode;
    }
}
