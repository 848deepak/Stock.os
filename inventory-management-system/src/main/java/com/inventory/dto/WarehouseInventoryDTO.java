package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInventoryDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Integer reorderLevel;
    private LocalDateTime lastStockedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
