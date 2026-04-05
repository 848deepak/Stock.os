package com.inventory.repository;

import com.inventory.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    Optional<Product> findByBarcode(String barcode);

    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    Page<Product> findByCategory_NameIgnoreCaseAndIsActiveTrue(String category, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findBySupplierId(Long supplierId);
    List<Product> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.quantity <= p.reorderLevel")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

    boolean existsBySku(String sku);
}
