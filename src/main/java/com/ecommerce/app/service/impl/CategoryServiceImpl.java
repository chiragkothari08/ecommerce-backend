package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.CategoryRequest;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.CategoryRepository;
import com.ecommerce.app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> listActive() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    @Override
    public UUID create(CategoryRequest request) {
        Category category = new Category();
        apply(category, request);
        return categoryRepository.save(category).getId();
    }

    @Override
    public void update(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> ApiException.notFound("Category not found"));
        apply(category, request);
        categoryRepository.save(category);
    }

    @Override
    public void delete(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> ApiException.notFound("Category not found"));
        category.setActive(false);
        categoryRepository.save(category);
    }

    private void apply(Category category, CategoryRequest request) {
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> ApiException.notFound("Parent category not found"));
            category.setParent(parent);
        }
    }
}
