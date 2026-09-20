package com.ecommerce.app.dto.request;

import com.ecommerce.app.entity.Coupon;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class CouponRequest {
    private String code;
    private String description;
    private Coupon.DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountAmount;
    private Instant validFrom;
    private Instant validUntil;
    private Integer usageLimit;
}
