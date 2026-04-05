package com.inventory.service;

import com.inventory.dto.CategoryDTO;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.Category;
import com.inventory.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing product categories.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    public CategoryDTO createCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setActive(true);

        Category saved = categoryRepository.save(category);
        auditLogService.log("Category", saved.getId(), "CREATE", null, saved.getName());
        return mapToDTO(saved);
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToDTO(category);
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getActiveCategories() {
        return categoryRepository.findByIsActive(true)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        String oldValue = category.getName();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setActive(dto.isActive());

        Category updated = categoryRepository.save(category);
        auditLogService.log("Category", id, "UPDATE", oldValue, dto.getName());
        return mapToDTO(updated);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryRepository.delete(category);
        auditLogService.log("Category", id, "DELETE", category.getName(), null);
    }

    private CategoryDTO mapToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
