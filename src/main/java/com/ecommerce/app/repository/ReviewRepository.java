package com.ecommerce.app.repository;

import com.ecommerce.app.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByProductId(UUID productId, Pageable pageable);
    List<Review> findByProductId(UUID productId);
    boolean existsByProductIdAndUserId(UUID productId, UUID userId);
}
