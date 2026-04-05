package com.inventory.service;

import com.inventory.dto.WarehouseInventoryDTO;
import com.inventory.exception.InventoryException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.Product;
import com.inventory.model.Warehouse;
import com.inventory.model.WarehouseInventory;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.WarehouseInventoryRepository;
import com.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing multi-warehouse inventory operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseInventoryService {
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final AuditLogService auditLogService;

    public WarehouseInventoryDTO createInventory(Long productId, Long warehouseId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        WarehouseInventory inventory = new WarehouseInventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(quantity);
        inventory.setLastStockedAt(LocalDateTime.now());

        WarehouseInventory saved = warehouseInventoryRepository.save(inventory);
        return mapToDTO(saved);
    }

    public void addStock(Long productId, Long warehouseId, Integer quantity) {
        WarehouseInventory inventory = warehouseInventoryRepository
                .findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse inventory not found"));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setLastStockedAt(LocalDateTime.now());
        warehouseInventoryRepository.save(inventory);

        // Update product's total quantity
        updateProductTotalQuantity(productId);
        auditLogService.log("WarehouseInventory", inventory.getId(), "UPDATE", null, "Stock added: " + quantity);
    }

    public void removeStock(Long productId, Long warehouseId, Integer quantity) {
        WarehouseInventory inventory = warehouseInventoryRepository
                .findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse inventory not found"));

        if (inventory.getQuantity() < quantity) {
            throw new InventoryException("Insufficient stock. Available: " + inventory.getQuantity());
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        warehouseInventoryRepository.save(inventory);
        updateProductTotalQuantity(productId);
        auditLogService.log("WarehouseInventory", inventory.getId(), "UPDATE", null, "Stock removed: " + quantity);
    }

    public List<WarehouseInventoryDTO> getInventoryByProduct(Long productId) {
        return warehouseInventoryRepository.findByProductId(productId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<WarehouseInventoryDTO> getInventoryByWarehouse(Long warehouseId) {
        return warehouseInventoryRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void updateProductTotalQuantity(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow();

        Integer totalQty = warehouseInventoryRepository.findByProductId(productId)
                .stream()
                .mapToInt(WarehouseInventory::getQuantity)
                .sum();

        product.setQuantity(totalQty);
        productRepository.save(product);
    }

    private WarehouseInventoryDTO mapToDTO(WarehouseInventory inventory) {
        return new WarehouseInventoryDTO(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                inventory.getWarehouse().getId(),
                inventory.getWarehouse().getName(),
                inventory.getQuantity(),
                inventory.getReorderLevel(),
                inventory.getLastStockedAt(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }
}
