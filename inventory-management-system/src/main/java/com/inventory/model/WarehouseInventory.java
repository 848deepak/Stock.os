package com.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * WarehouseInventory entity representing product inventory at specific warehouses.
 * Enables multi-warehouse inventory tracking with warehouse-specific quantities.
 */
@Entity
@Table(name = "warehouse_inventory", indexes = {
        @Index(name = "idx_warehouse_id", columnList = "warehouse_id"),
        @Index(name = "idx_product_id", columnList = "product_id")
},
uniqueConstraints = {
        @UniqueConstraint(name = "unique_product_warehouse", columnNames = {"product_id", "warehouse_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "reorder_level")
    private Integer reorderLevel = 10;

    @Column(name = "last_stocked_at")
    private LocalDateTime lastStockedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
