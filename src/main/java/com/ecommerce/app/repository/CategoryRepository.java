package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByActiveTrueOrderBySortOrderAsc();
    List<Category> findByParentIsNull();
}
