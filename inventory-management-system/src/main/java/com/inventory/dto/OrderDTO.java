package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private String status;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDelivery;
    private LocalDateTime actualDelivery;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
