package com.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * StockAlert entity for tracking and managing low stock and critical inventory alerts.
 * Enables automated alert generation for inventory management.
 */
@Entity
@Table(name = "stock_alerts", indexes = {
        @Index(name = "idx_product_id", columnList = "product_id"),
        @Index(name = "idx_is_resolved", columnList = "is_resolved"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(name = "alert_type", length = 50)
    private String alertType; // LOW_STOCK, CRITICAL_STOCK, OUT_OF_STOCK, OVERSTOCK

    @Column(name = "current_stock")
    private Integer currentStock;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "is_resolved", nullable = false)
    private boolean isResolved = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void setProductId(Long productId) {
        if (productId == null) {
            this.product = null;
            return;
        }
        Product p = new Product();
        p.setId(productId);
        this.product = p;
    }

    public Long getProductId() {
        return this.product != null ? this.product.getId() : null;
    }

    public void setWarehouseId(Long warehouseId) {
        if (warehouseId == null) {
            this.warehouse = null;
            return;
        }
        Warehouse w = new Warehouse();
        w.setId(warehouseId);
        this.warehouse = w;
    }

    public Long getWarehouseId() {
        return this.warehouse != null ? this.warehouse.getId() : null;
    }
}
