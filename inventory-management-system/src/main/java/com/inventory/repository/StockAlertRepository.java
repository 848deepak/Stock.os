package com.inventory.repository;

import com.inventory.model.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    List<StockAlert> findByIsResolved(boolean isResolved);
    List<StockAlert> findByProduct_Id(Long productId);
    List<StockAlert> findByWarehouse_Id(Long warehouseId);
    List<StockAlert> findByProduct_IdAndIsResolved(Long productId, boolean isResolved);
}
