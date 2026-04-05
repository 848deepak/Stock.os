package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAlertDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private String alertType;
    private Integer currentStock;
    private Integer reorderLevel;
    private boolean isResolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
