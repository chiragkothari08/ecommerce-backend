package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.CreateOrderRequest;
import com.ecommerce.app.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.app.dto.response.OrderResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.*;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.*;
import com.ecommerce.app.service.NotificationService;
import com.ecommerce.app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final BigDecimal FLAT_DELIVERY_FEE = new BigDecimal("49.00");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("999.00");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.00"); // adjust per GST rules if needed

    @Override
    @Transactional
    public Order createOrder(UUID userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> ApiException.badRequest("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw ApiException.badRequest("Cart is empty");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> ApiException.notFound("Address not found"));
        if (!address.getUser().getId().equals(userId)) {
            throw ApiException.forbidden("Address does not belong to this user");
        }

        // Validate stock and compute subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> ApiException.notFound("Product not found: " + item.getProduct().getTitle()));
            if (product.getStock() < item.getQuantity()) {
                throw ApiException.badRequest("Insufficient stock for: " + product.getTitle());
            }
            BigDecimal price = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
            subtotal = subtotal.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // Apply coupon
        BigDecimal discount = BigDecimal.ZERO;
        String couponCode = request.getCouponCode() != null ? request.getCouponCode() : cart.getAppliedCouponCode();
        if (couponCode != null) {
            Coupon coupon = couponRepository.findByCodeIgnoreCase(couponCode)
                    .orElseThrow(() -> ApiException.notFound("Invalid coupon"));
            if (coupon.getMinOrderValue() != null && subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
                throw ApiException.badRequest("Minimum order value not met for coupon");
            }
            discount = coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE
                    ? subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100))
                    : coupon.getDiscountValue();
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
            coupon.setUsageCount(coupon.getUsageCount() + 1);
            couponRepository.save(coupon);
        }

        BigDecimal deliveryFee = subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ? BigDecimal.ZERO : FLAT_DELIVERY_FEE;
        BigDecimal tax = subtotal.subtract(discount).multiply(TAX_RATE);
        BigDecimal total = subtotal.subtract(discount).add(deliveryFee).add(tax);

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setDeliveryAddress(address);
        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setDeliveryFee(deliveryFee);
        order.setTax(tax);
        order.setTotal(total);
        order.setCouponCode(couponCode);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PLACED);
        order.setPaid("COD".equalsIgnoreCase(request.getPaymentMethod())); // COD marked unpaid-on-delivery in real apps; adjust as needed
        order = orderRepository.save(order);

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setVariant(item.getVariant());
            orderItem.setProductTitleSnapshot(product.getTitle());
            orderItem.setPriceSnapshot(product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemRepository.save(orderItem);

            // decrement stock
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // clear cart after order placed
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setAppliedCouponCode(null);
        cartRepository.save(cart);

        notificationService.send(userId, "Order Placed",
                "Your order " + order.getOrderNumber() + " has been placed successfully.", "ORDER_UPDATE", order.getId().toString());

        return order;
    }

    @Override
    public OrderResponse getOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));
        return toResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> listOrders(UUID userId, Pageable pageable) {
        Page<Order> page = orderRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Override
    @Transactional
    public void cancelOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw ApiException.badRequest("Order cannot be cancelled at this stage");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // restock items
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        notificationService.send(userId, "Order Cancelled",
                "Your order " + order.getOrderNumber() + " has been cancelled.", "ORDER_UPDATE", order.getId().toString());
    }

    @Override
    @Transactional
    public void reorder(UUID userId, UUID orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> ApiException.notFound("Order not found"));

        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            User u = new User();
            u.setId(userId);
            c.setUser(u);
            return cartRepository.save(c);
        });

        for (OrderItem item : order.getItems()) {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(item.getProduct());
            cartItem.setVariant(item.getVariant());
            cartItem.setQuantity(item.getQuantity());
            cartItemRepository.save(cartItem);
        }
    }

    @Override
    @Transactional
    public void updateStatus(UUID orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> ApiException.notFound("Order not found"));
        order.setStatus(request.getStatus());
        orderRepository.save(order);

        notificationService.send(order.getUser().getId(), "Order Update",
                "Your order " + order.getOrderNumber() + " is now " + request.getStatus(), "ORDER_UPDATE", order.getId().toString());
    }

    @Override
    public PageResponse<OrderResponse> listAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    private String generateOrderNumber() {
        return "ORD" + Instant.now().toEpochMilli() + (int) (Math.random() * 900 + 100);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse r = new OrderResponse();
        r.setId(order.getId());
        r.setOrderNumber(order.getOrderNumber());
        r.setStatus(order.getStatus());
        r.setSubtotal(order.getSubtotal());
        r.setDiscount(order.getDiscount());
        r.setDeliveryFee(order.getDeliveryFee());
        r.setTax(order.getTax());
        r.setTotal(order.getTotal());
        r.setPaymentMethod(order.getPaymentMethod());
        r.setPaid(order.isPaid());
        r.setCreatedAt(order.getCreatedAt());
        r.setItems(order.getItems().stream().map(i -> {
            OrderResponse.OrderItemResponse ir = new OrderResponse.OrderItemResponse();
            ir.setProductId(i.getProduct().getId());
            ir.setProductTitle(i.getProductTitleSnapshot());
            ir.setPrice(i.getPriceSnapshot());
            ir.setQuantity(i.getQuantity());
            return ir;
        }).toList());
        return r;
    }
}
