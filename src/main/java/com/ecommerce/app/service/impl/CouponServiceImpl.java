package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.CouponRequest;
import com.ecommerce.app.entity.Coupon;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.CouponRepository;
import com.ecommerce.app.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public List<Coupon> listActive() {
        return couponRepository.findByActiveTrue();
    }

    @Override
    public Coupon validate(String code, BigDecimal cartTotal) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> ApiException.notFound("Invalid coupon code"));

        if (!coupon.isActive()) throw ApiException.badRequest("Coupon is no longer active");

        Instant now = Instant.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            throw ApiException.badRequest("Coupon is not yet valid");
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            throw ApiException.badRequest("Coupon has expired");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw ApiException.badRequest("Coupon usage limit reached");
        }
        if (coupon.getMinOrderValue() != null && cartTotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw ApiException.badRequest("Minimum order value not met for this coupon");
        }
        return coupon;
    }

    @Override
    public UUID create(CouponRequest request) {
        Coupon coupon = new Coupon();
        apply(coupon, request);
        return couponRepository.save(coupon).getId();
    }

    @Override
    public void update(UUID id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> ApiException.notFound("Coupon not found"));
        apply(coupon, request);
        couponRepository.save(coupon);
    }

    @Override
    public void delete(UUID id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> ApiException.notFound("Coupon not found"));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> listAll() {
        return couponRepository.findAll();
    }

    private void apply(Coupon coupon, CouponRequest request) {
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        coupon.setUsageLimit(request.getUsageLimit());
    }
}
