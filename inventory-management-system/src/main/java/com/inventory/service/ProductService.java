package com.inventory.service;

import com.inventory.dto.ProductDTO;
import com.inventory.exception.InventoryException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.Category;
import com.inventory.model.Product;
import com.inventory.repository.CategoryRepository;
import com.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public ProductDTO createProduct(ProductDTO dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new InventoryException("Product with SKU " + dto.getSku() + " already exists");
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setDescription(dto.getDescription());
        product.setCategory(resolveCategory(dto.getCategory()));
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setReorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 10);
        product.setExpiryDate(dto.getExpiryDate());
        product.setIsActive(true);
        product.setCreatedBy(getCurrentUsername());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getSku().equals(dto.getSku()) && productRepository.existsBySku(dto.getSku())) {
            throw new InventoryException("Product with SKU " + dto.getSku() + " already exists");
        }

        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setDescription(dto.getDescription());
        product.setCategory(resolveCategory(dto.getCategory()));
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setReorderLevel(dto.getReorderLevel());
        product.setExpiryDate(dto.getExpiryDate());
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setIsActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getIsActive()) {
            throw new ResourceNotFoundException("Product is not active");
        }

        return convertToDTO(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("", pageable)
            .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        return productRepository.search(keyword, pageable)
            .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory_NameIgnoreCaseAndIsActiveTrue(category, pageable)
            .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public ProductDTO mapToDTO(Product product) {
        return convertToDTO(product);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setReorderLevel(product.getReorderLevel());
        dto.setExpiryDate(product.getExpiryDate());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedBy(product.getCreatedBy());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setIsLowStock(product.isLowStock());
        return dto;
    }

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Category resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }
        return categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new InventoryException("Category not found: " + categoryName));
    }
}
