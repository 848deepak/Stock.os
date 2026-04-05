package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import com.inventory.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for data export functionality.
 */
@RestController
@RequestMapping("/exports")
@RequiredArgsConstructor
public class ExportController {
    private final ExportService exportService;
    private final ProductRepository productRepository;

    @GetMapping("/products/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<byte[]> exportProductsToCSV(
            @RequestParam(required = false) String category) {

        List<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategoryId(Long.parseLong(category));
        } else {
            products = productRepository.findAll();
        }

        byte[] csvData = exportService.exportProductsToCSV(products);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"products.csv\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvData);
    }

    @GetMapping("/products/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<byte[]> exportProductsToPDF(
            @RequestParam(required = false) String category) {

        List<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategoryId(Long.parseLong(category));
        } else {
            products = productRepository.findAll();
        }

        byte[] pdfData = exportService.exportProductsToPDF(products);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"products.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    @GetMapping("/transactions/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<byte[]> exportTransactionsToPDF(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {

        byte[] pdfData = exportService.exportInventoryTransactionsToPDF(startDate, endDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
}
