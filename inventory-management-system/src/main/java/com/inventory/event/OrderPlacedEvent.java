package com.inventory.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when an order is placed.
 * Triggers automatic inventory deduction.
 */
@Getter
@ToString
public class OrderPlacedEvent extends ApplicationEvent {
    private final Long orderId;
    private final Long productId;
    private final Long warehouseId;
    private final Integer quantity;

    public OrderPlacedEvent(Object source, Long orderId, Long productId, Long warehouseId, Integer quantity) {
        super(source);
        this.orderId = orderId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
    }
}
