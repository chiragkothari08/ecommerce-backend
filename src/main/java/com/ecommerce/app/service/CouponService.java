package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.CouponRequest;
import com.ecommerce.app.entity.Coupon;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CouponService {
    List<Coupon> listActive();
    Coupon validate(String code, BigDecimal cartTotal);
    UUID create(CouponRequest request);
    void update(UUID id, CouponRequest request);
    void delete(UUID id);
    List<Coupon> listAll();
}
