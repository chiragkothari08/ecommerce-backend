package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.ApplyCouponRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.Coupon;
import com.ecommerce.app.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ApiResponse<List<Coupon>> list() {
        return ApiResponse.ok(couponService.listActive());
    }

    @PostMapping("/validate")
    public ApiResponse<Coupon> validate(@Valid @RequestBody ApplyCouponRequest request,
                                         @RequestParam(defaultValue = "0") BigDecimal cartTotal) {
        return ApiResponse.ok(couponService.validate(request.getCode(), cartTotal));
    }
}
