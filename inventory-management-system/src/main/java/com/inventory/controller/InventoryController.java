package com.inventory.controller;

import com.inventory.dto.InventoryTransactionDTO;
import com.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/stock-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryTransactionDTO> stockIn(@Valid @RequestBody InventoryTransactionDTO dto) {
        InventoryTransactionDTO transaction = inventoryService.stockIn(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/stock-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryTransactionDTO> stockOut(@Valid @RequestBody InventoryTransactionDTO dto) {
        InventoryTransactionDTO transaction = inventoryService.stockOut(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/adjustment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<InventoryTransactionDTO> adjustmentStock(@Valid @RequestBody InventoryTransactionDTO dto) {
        InventoryTransactionDTO transaction = inventoryService.adjustmentStock(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<Map<String, Object>> getTransactionHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryTransactionDTO> transactions = inventoryService.getTransactionHistory(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", transactions.getContent());
        response.put("totalElements", transactions.getTotalElements());
        response.put("totalPages", transactions.getTotalPages());
        response.put("currentPage", transactions.getNumber());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<Map<String, Object>> getProductTransactions(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryTransactionDTO> transactions = inventoryService.getProductTransactions(productId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", transactions.getContent());
        response.put("totalElements", transactions.getTotalElements());
        response.put("totalPages", transactions.getTotalPages());
        response.put("currentPage", transactions.getNumber());

        return ResponseEntity.ok(response);
    }
}
