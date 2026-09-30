package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.AddToCartRequest;
import com.ecommerce.app.dto.request.ApplyCouponRequest;
import com.ecommerce.app.dto.request.UpdateCartItemRequest;
import com.ecommerce.app.dto.response.CartResponse;
import com.ecommerce.app.entity.*;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.*;
import com.ecommerce.app.service.CartService;
import com.ecommerce.app.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponService couponService;

    @Override
    public CartResponse getCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> ApiException.notFound("Product not found"));

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> ApiException.notFound("Variant not found"));
        }

        UUID variantId = variant == null ? null : variant.getId();
        var existing = cartItemRepository.findByCartIdAndProductIdAndVariantId(cart.getId(), product.getId(), variantId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setVariant(variant);
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }
        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public CartResponse updateItem(UUID userId, UUID itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream().filter(i -> i.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> ApiException.notFound("Cart item not found"));
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return toResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public void removeItem(UUID userId, UUID itemId) {
        Cart cart = getOrCreateCart(userId);
        boolean owns = cart.getItems().stream().anyMatch(i -> i.getId().equals(itemId));
        if (!owns) throw ApiException.notFound("Cart item not found");
        cartItemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setAppliedCouponCode(null);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartResponse applyCoupon(UUID userId, ApplyCouponRequest request) {
        Cart cart = getOrCreateCart(userId);
        BigDecimal subtotal = calculateSubtotal(cart);
        Coupon coupon = couponService.validate(request.getCode(), subtotal);
        cart.setAppliedCouponCode(coupon.getCode());
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override
    @Transactional
    public void removeCoupon(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setAppliedCouponCode(null);
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            User u = new User();
            u.setId(userId);
            cart.setUser(u);
            return cartRepository.save(cart);
        });
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getItems().stream()
                .map(i -> effectivePrice(i.getProduct()).multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal effectivePrice(Product p) {
        return p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice();
    }

    private CartResponse toResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setAppliedCouponCode(cart.getAppliedCouponCode());

        var items = cart.getItems().stream().map(i -> {
            CartResponse.CartItemResponse ir = new CartResponse.CartItemResponse();
            ir.setItemId(i.getId());
            ir.setProductId(i.getProduct().getId());
            ir.setProductTitle(i.getProduct().getTitle());
            ir.setProductImage(i.getProduct().getImages().isEmpty() ? null : i.getProduct().getImages().get(0).getUrl());
            ir.setVariantId(i.getVariant() != null ? i.getVariant().getId() : null);
            ir.setVariantLabel(i.getVariant() != null ? (i.getVariant().getSize() + "/" + i.getVariant().getColor()) : null);
            BigDecimal price = effectivePrice(i.getProduct());
            ir.setPrice(price);
            ir.setQuantity(i.getQuantity());
            ir.setLineTotal(price.multiply(BigDecimal.valueOf(i.getQuantity())));
            return ir;
        }).toList();

        response.setItems(items);
        BigDecimal subtotal = items.stream().map(CartResponse.CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setSubtotal(subtotal);

        BigDecimal discount = BigDecimal.ZERO;
        if (cart.getAppliedCouponCode() != null) {
            try {
                Coupon coupon = couponService.validate(cart.getAppliedCouponCode(), subtotal);
                discount = coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE
                        ? subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100))
                        : coupon.getDiscountValue();
                if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                    discount = coupon.getMaxDiscountAmount();
                }
            } catch (Exception ignored) {
                // coupon no longer valid, ignore in display
            }
        }
        response.setDiscount(discount);
        response.setTotal(subtotal.subtract(discount));
        return response;
    }
}
