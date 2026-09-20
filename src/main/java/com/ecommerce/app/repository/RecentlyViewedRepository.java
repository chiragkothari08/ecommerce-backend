package com.ecommerce.app.repository;

import com.ecommerce.app.entity.RecentlyViewed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewed, UUID> {
    List<RecentlyViewed> findTop10ByUserIdOrderByUpdatedAtDesc(UUID userId);
    Optional<RecentlyViewed> findByUserIdAndProductId(UUID userId, UUID productId);
}
