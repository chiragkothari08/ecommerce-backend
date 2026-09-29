package com.ecommerce.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_images")
public class ProductImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Optional link to a specific color/size variant. NULL means this image
    // applies generally to the product (not tied to one specific color).
    // This is what lets the frontend swap the displayed photos when the
    // user selects a different color.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(nullable = false)
    private String url;

    private String cloudinaryPublicId;

    @Column(nullable = false)
    private int sortOrder = 0;
}
