package com.inventory.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when stock reaches or drops below reorder level.
 * Triggers automatic low stock alerts.
 */
@Getter
@ToString
public class StockLowEvent extends ApplicationEvent {
    private final Long productId;
    private final Long warehouseId;
    private final Integer currentStock;
    private final Integer reorderLevel;

    public StockLowEvent(Object source, Long productId, Long warehouseId, Integer currentStock, Integer reorderLevel) {
        super(source);
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
    }
}
