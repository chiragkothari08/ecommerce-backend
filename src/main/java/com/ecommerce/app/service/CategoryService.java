package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.CategoryRequest;
import com.ecommerce.app.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<Category> listActive();
    UUID create(CategoryRequest request);
    void update(UUID id, CategoryRequest request);
    void delete(UUID id);
}
