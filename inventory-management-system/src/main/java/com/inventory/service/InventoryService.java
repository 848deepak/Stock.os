package com.inventory.service;

import com.inventory.dto.InventoryTransactionDTO;
import com.inventory.exception.InventoryException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.InventoryTransaction;
import com.inventory.model.Product;
import com.inventory.model.User;
import com.inventory.repository.InventoryTransactionRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    public InventoryTransactionDTO stockIn(InventoryTransactionDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

        if (dto.getQuantity() <= 0) {
            throw new InventoryException("Stock in quantity must be greater than 0");
        }

        product.setQuantity(product.getQuantity() + dto.getQuantity());
        productRepository.save(product);

        return recordTransaction(product, InventoryTransaction.TransactionType.STOCK_IN, dto.getQuantity(), dto.getReason());
    }

    public InventoryTransactionDTO stockOut(InventoryTransactionDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

        if (dto.getQuantity() <= 0) {
            throw new InventoryException("Stock out quantity must be greater than 0");
        }

        if (product.getQuantity() < dto.getQuantity()) {
            throw new InventoryException("Insufficient stock. Available: " + product.getQuantity() +
                ", Requested: " + dto.getQuantity());
        }

        product.setQuantity(product.getQuantity() - dto.getQuantity());
        productRepository.save(product);

        return recordTransaction(product, InventoryTransaction.TransactionType.STOCK_OUT,
            dto.getQuantity(), dto.getReason());
    }

    public InventoryTransactionDTO adjustmentStock(InventoryTransactionDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));

        product.setQuantity(dto.getQuantity());
        productRepository.save(product);

        return recordTransaction(product, InventoryTransaction.TransactionType.ADJUSTMENT,
            dto.getQuantity(), dto.getReason());
    }

    @Transactional(readOnly = true)
    public Page<InventoryTransactionDTO> getTransactionHistory(Pageable pageable) {
        return transactionRepository.findAll(pageable)
            .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<InventoryTransactionDTO> getProductTransactions(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        return transactionRepository.findByProductId(productId, pageable)
            .map(this::convertToDTO);
    }

    private InventoryTransactionDTO recordTransaction(Product product,
                                                       InventoryTransaction.TransactionType type,
                                                       Integer quantity, String reason) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProduct(product);
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setReason(reason);
        transaction.setPerformedBy(user);
        transaction.setReferenceId(UUID.randomUUID().toString());
        transaction.setCreatedAt(LocalDateTime.now());

        InventoryTransaction saved = transactionRepository.save(transaction);
        return convertToDTO(saved);
    }

    private InventoryTransactionDTO convertToDTO(InventoryTransaction transaction) {
        InventoryTransactionDTO dto = new InventoryTransactionDTO();
        dto.setId(transaction.getId());
        dto.setProductId(transaction.getProduct().getId());
        dto.setProductName(transaction.getProduct().getName());
        dto.setType(transaction.getType().toString());
        dto.setQuantity(transaction.getQuantity());
        dto.setReason(transaction.getReason());
        dto.setPerformedByUsername(transaction.getPerformedBy() != null ? transaction.getPerformedBy().getUsername() : null);
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setReferenceId(transaction.getReferenceId());
        return dto;
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
