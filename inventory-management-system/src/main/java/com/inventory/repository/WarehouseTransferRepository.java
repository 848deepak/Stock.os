package com.inventory.repository;

import com.inventory.model.WarehouseTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseTransferRepository extends JpaRepository<WarehouseTransfer, Long> {
    List<WarehouseTransfer> findByStatus(String status);
    List<WarehouseTransfer> findByProductId(Long productId);
    List<WarehouseTransfer> findByFromWarehouseId(Long warehouseId);
    List<WarehouseTransfer> findByToWarehouseId(Long warehouseId);
}
