package com.inventory.repository;

import com.inventory.model.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    Page<InventoryTransaction> findByProductId(Long productId, Pageable pageable);

    List<InventoryTransaction> findByCreatedAtAfter(LocalDateTime createdAt);

    Page<InventoryTransaction> findByType(InventoryTransaction.TransactionType type, Pageable pageable);

    Page<InventoryTransaction> findAll(Pageable pageable);
}
