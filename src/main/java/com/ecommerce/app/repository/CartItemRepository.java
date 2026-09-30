package com.ecommerce.app.repository;

import com.ecommerce.app.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartIdAndProductIdAndVariantId(UUID cartId, UUID productId, UUID variantId);
    void deleteByCartId(UUID cartId);
}
