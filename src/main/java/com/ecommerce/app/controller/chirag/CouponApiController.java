package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.CouponApplyRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.Coupon;
import com.ecommerce.app.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Module 6: Coupons & Discounts
 * POST /coupons/apply   (validate coupon code + calculate discount)
 */
@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponApiController {

    private final CouponService couponService;

    @PostMapping("/apply")
    public ApiResponse<Map<String, Object>> applyCoupon(@Valid @RequestBody CouponApplyRequest request) {
        Coupon coupon = couponService.validate(request.getCode(), request.getCartTotal());

        BigDecimal discount = coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE
                ? request.getCartTotal().multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100))
                : coupon.getDiscountValue();

        if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discount = coupon.getMaxDiscountAmount();
        }

        BigDecimal finalTotal = request.getCartTotal().subtract(discount);

        return ApiResponse.ok("Coupon applied", Map.of(
                "code", coupon.getCode(),
                "discountType", coupon.getDiscountType(),
                "discountAmount", discount,
                "cartTotal", request.getCartTotal(),
                "finalTotal", finalTotal
        ));
    }
}
