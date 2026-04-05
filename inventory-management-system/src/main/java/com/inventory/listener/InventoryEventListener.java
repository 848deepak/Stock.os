package com.inventory.listener;

import com.inventory.event.OrderPlacedEvent;
import com.inventory.event.OrderCancelledEvent;
import com.inventory.event.StockLowEvent;
import com.inventory.model.StockAlert;
import com.inventory.repository.StockAlertRepository;
import com.inventory.service.WarehouseInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Event listeners for automated inventory management.
 * Responds to order and stock events to update inventory automatically.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {
    private final WarehouseInventoryService warehouseInventoryService;
    private final StockAlertRepository stockAlertRepository;

    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Processing order placed event: orderId={}, productId={}, quantity={}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        try {
            // Automatically deduct inventory when order is placed
            warehouseInventoryService.removeStock(
                    event.getProductId(),
                    event.getWarehouseId(),
                    event.getQuantity()
            );
            log.info("Inventory deducted successfully for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process order placed event", e);
            throw new RuntimeException("Order processing failed: " + e.getMessage());
        }
    }

    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("Processing order cancelled event: orderId={}, productId={}, quantity={}",
                event.getOrderId(), event.getProductId(), event.getQuantity());

        try {
            // Automatically restore inventory when order is cancelled
            warehouseInventoryService.addStock(
                    event.getProductId(),
                    event.getWarehouseId(),
                    event.getQuantity()
            );
            log.info("Inventory restored successfully for cancelled order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process order cancelled event", e);
            throw new RuntimeException("Cancellation processing failed: " + e.getMessage());
        }
    }

    @EventListener
    public void handleStockLow(StockLowEvent event) {
        log.info("Processing stock low event: productId={}, currentStock={}, reorderLevel={}",
                event.getProductId(), event.getCurrentStock(), event.getReorderLevel());

        try {
            // Create alert for low stock
            StockAlert alert = new StockAlert();
            alert.setProductId(event.getProductId());
            alert.setWarehouseId(event.getWarehouseId());
            alert.setAlertType("LOW_STOCK");
            alert.setCurrentStock(event.getCurrentStock());
            alert.setReorderLevel(event.getReorderLevel());
            alert.setResolved(false);
            alert.setCreatedAt(LocalDateTime.now());

            stockAlertRepository.save(alert);
            log.info("Stock alert created for product: {}", event.getProductId());
        } catch (Exception e) {
            log.error("Failed to process stock low event", e);
        }
    }
}
