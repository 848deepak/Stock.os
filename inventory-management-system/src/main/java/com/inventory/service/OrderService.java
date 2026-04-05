package com.inventory.service;

import com.inventory.dto.OrderDTO;
import com.inventory.event.OrderCancelledEvent;
import com.inventory.event.OrderPlacedEvent;
import com.inventory.exception.InventoryException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.Order;
import com.inventory.model.Product;
import com.inventory.model.Warehouse;
import com.inventory.repository.OrderRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing orders and triggering inventory automation.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryService warehouseInventoryService;
    private final ApplicationEventPublisher eventPublisher;
    private final AuditLogService auditLogService;

    public OrderDTO createOrder(OrderDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setProduct(product);
        order.setWarehouse(warehouse);
        order.setQuantity(dto.getQuantity());
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());
        order.setExpectedDelivery(dto.getExpectedDelivery());

        Order saved = orderRepository.save(order);
        auditLogService.log("Order", saved.getId(), "CREATE", null, saved.getOrderNumber());

        // Publish event for inventory automation
        eventPublisher.publishEvent(new OrderPlacedEvent(this, saved.getId(), product.getId(), warehouse.getId(), dto.getQuantity()));

        return mapToDTO(saved);
    }

    public OrderDTO confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus("CONFIRMED");
        Order updated = orderRepository.save(order);
        auditLogService.log("Order", orderId, "UPDATE", "PENDING", "CONFIRMED");

        return mapToDTO(updated);
    }

    public OrderDTO shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus("SHIPPED");
        Order updated = orderRepository.save(order);
        auditLogService.log("Order", orderId, "UPDATE", "CONFIRMED", "SHIPPED");

        return mapToDTO(updated);
    }

    public OrderDTO deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus("DELIVERED");
        order.setActualDelivery(LocalDateTime.now());
        Order updated = orderRepository.save(order);
        auditLogService.log("Order", orderId, "UPDATE", "SHIPPED", "DELIVERED");

        return mapToDTO(updated);
    }

    public OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new InventoryException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        Order updated = orderRepository.save(order);
        auditLogService.log("Order", orderId, "UPDATE", order.getStatus(), "CANCELLED");

        // Publish event for inventory restoration
        eventPublisher.publishEvent(new OrderCancelledEvent(this, orderId, order.getProduct().getId(),
                order.getWarehouse().getId(), order.getQuantity()));

        return mapToDTO(updated);
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToDTO(order);
    }

    private OrderDTO mapToDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getProduct().getId(),
                order.getProduct().getName(),
                order.getWarehouse() != null ? order.getWarehouse().getId() : null,
                order.getWarehouse() != null ? order.getWarehouse().getName() : null,
                order.getQuantity(),
                order.getStatus(),
                order.getOrderDate(),
                order.getExpectedDelivery(),
                order.getActualDelivery(),
                order.getNotes(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
