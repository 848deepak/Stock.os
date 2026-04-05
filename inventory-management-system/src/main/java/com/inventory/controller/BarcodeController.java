package com.inventory.controller;

import com.inventory.dto.ProductDTO;
import com.inventory.model.Product;
import com.inventory.service.BarcodeService;
import com.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for barcode generation and scanning.
 */
@RestController
@RequestMapping("/barcodes")
@RequiredArgsConstructor
public class BarcodeController {
    private final BarcodeService barcodeService;
    private final ProductService productService;

    @PostMapping("/generate/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, String>> generateBarcode(@PathVariable Long productId) {
        String barcodeValue = barcodeService.generateBarcodeForProduct(productId);
        Map<String, String> response = new HashMap<>();
        response.put("barcode", barcodeValue);
        response.put("productId", String.valueOf(productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/image/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<Map<String, String>> getBarcodeImage(@PathVariable Long productId) {
        String imageData = barcodeService.getBarcodeImage(productId);
        Map<String, String> response = new HashMap<>();
        response.put("image", imageData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/regenerate/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, String>> regenerateBarcode(@PathVariable Long productId) {
        String newBarcode = barcodeService.regenerateBarcode(productId);
        Map<String, String> response = new HashMap<>();
        response.put("barcode", newBarcode);
        response.put("productId", String.valueOf(productId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/scan/{barcode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ProductDTO> scanBarcode(@PathVariable String barcode) {
        Product product = barcodeService.getProductByBarcode(barcode);
        return ResponseEntity.ok(productService.mapToDTO(product));
    }
}
