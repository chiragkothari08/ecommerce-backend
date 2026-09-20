package com.ecommerce.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "coupons")
public class Coupon extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // PERCENTAGE, FLAT

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderValue;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    private Instant validFrom;

    private Instant validUntil;

    private Integer usageLimit;

    @Column(nullable = false)
    private Integer usageCount = 0;

    @Column(nullable = false)
    private boolean active = true;

    public enum DiscountType { PERCENTAGE, FLAT }
}
