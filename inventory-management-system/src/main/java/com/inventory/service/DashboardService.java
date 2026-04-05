package com.inventory.service;

import com.inventory.repository.InventoryTransactionRepository;
import com.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total products
        stats.put("totalProducts", productRepository.count());

        // Low stock products
        stats.put("lowStockProducts", productRepository.findLowStockProducts().size());

        // Recent transactions
        stats.put("recentTransactions", transactionRepository
            .findByCreatedAtAfter(LocalDateTime.now().minusHours(24)).size());

        return stats;
    }
}
