package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.response.ProductResponse;
import com.ecommerce.app.entity.*;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.*;
import com.ecommerce.app.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public List<ProductResponse> getWishlist(UUID userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(w -> ProductResponse.from(w.getProduct()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addToWishlist(UUID userId, UUID productId) {
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent()) return;
        Product product = productRepository.findById(productId).orElseThrow(() -> ApiException.notFound("Product not found"));
        Wishlist wishlist = new Wishlist();
        User u = new User();
        u.setId(userId);
        wishlist.setUser(u);
        wishlist.setProduct(product);
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(UUID userId, UUID productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional
    public void moveToCart(UUID userId, UUID productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> ApiException.notFound("Item not in wishlist"));

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            User u = new User();
            u.setId(userId);
            c.setUser(u);
            return cartRepository.save(c);
        });

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(wishlist.getProduct());
        item.setQuantity(1);
        cartItemRepository.save(item);

        wishlistRepository.delete(wishlist);
    }
}
