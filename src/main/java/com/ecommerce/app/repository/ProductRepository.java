package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    List<Product> findByFeaturedTrueAndActiveTrue();
    List<Product> findByFlashSaleTrueAndActiveTrue();
    List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);
    List<Product> findTop10ByActiveTrueOrderByCreatedAtDesc();
}
