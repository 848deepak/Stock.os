package com.inventory.service;

import com.inventory.dto.WarehouseDTO;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.Warehouse;
import com.inventory.model.User;
import com.inventory.repository.WarehouseRepository;
import com.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing warehouses and warehouse operations.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public WarehouseDTO createWarehouse(WarehouseDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : 10000);
        warehouse.setActive(true);

        if (dto.getManagerName() != null) {
            userRepository.findByUsername(dto.getManagerName())
                    .ifPresent(warehouse::setManager);
        }

        Warehouse saved = warehouseRepository.save(warehouse);
        auditLogService.log("Warehouse", saved.getId(), "CREATE", null, saved.getName());
        return mapToDTO(saved);
    }

    public WarehouseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return mapToDTO(warehouse);
    }

    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<WarehouseDTO> getActiveWarehouses() {
        return warehouseRepository.findByIsActive(true)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO dto) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        String oldValue = warehouse.getName();
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setCapacity(dto.getCapacity() != null ? dto.getCapacity() : warehouse.getCapacity());
        warehouse.setActive(dto.isActive());

        if (dto.getManagerName() != null) {
            userRepository.findByUsername(dto.getManagerName())
                    .ifPresent(warehouse::setManager);
        }

        Warehouse updated = warehouseRepository.save(warehouse);
        auditLogService.log("Warehouse", id, "UPDATE", oldValue, dto.getName());
        return mapToDTO(updated);
    }

    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        warehouseRepository.delete(warehouse);
        auditLogService.log("Warehouse", id, "DELETE", warehouse.getName(), null);
    }

    private WarehouseDTO mapToDTO(Warehouse warehouse) {
        return new WarehouseDTO(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getLocation(),
                warehouse.getManager() != null ? warehouse.getManager().getUsername() : null,
                warehouse.getCapacity(),
                warehouse.isActive(),
                warehouse.getCreatedAt(),
                warehouse.getUpdatedAt()
        );
    }
}
