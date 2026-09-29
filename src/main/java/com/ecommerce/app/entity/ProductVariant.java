package com.ecommerce.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String size;

    private String color;

    private String sku;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceOverride;

    @Column(nullable = false)
    private Integer stock = 0;
}
