package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseTransferDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;
    private Integer quantity;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
