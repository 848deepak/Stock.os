package com.inventory.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when an order is cancelled.
 * Triggers automatic inventory restoration.
 */
@Getter
@ToString
public class OrderCancelledEvent extends ApplicationEvent {
    private final Long orderId;
    private final Long productId;
    private final Long warehouseId;
    private final Integer quantity;

    public OrderCancelledEvent(Object source, Long orderId, Long productId, Long warehouseId, Integer quantity) {
        super(source);
        this.orderId = orderId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
    }
}
