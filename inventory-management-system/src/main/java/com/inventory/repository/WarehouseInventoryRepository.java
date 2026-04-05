package com.inventory.repository;

import com.inventory.model.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Long> {
    Optional<WarehouseInventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    List<WarehouseInventory> findByProductId(Long productId);
    List<WarehouseInventory> findByWarehouseId(Long warehouseId);
    List<WarehouseInventory> findByQuantityLessThanEqual(Integer reorderLevel);
}
