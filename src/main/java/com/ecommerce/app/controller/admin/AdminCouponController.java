package com.ecommerce.app.controller.admin;

import com.ecommerce.app.dto.request.CouponRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.Coupon;
import com.ecommerce.app.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @PostMapping
    public ApiResponse<Map<String, UUID>> create(@Valid @RequestBody CouponRequest request) {
        return ApiResponse.ok("Coupon created", Map.of("id", couponService.create(request)));
    }

    @GetMapping
    public ApiResponse<List<Coupon>> list() {
        return ApiResponse.ok(couponService.listAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable UUID id, @Valid @RequestBody CouponRequest request) {
        couponService.update(id, request);
        return ApiResponse.ok("Coupon updated", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        couponService.delete(id);
        return ApiResponse.ok("Coupon deactivated", null);
    }
}
