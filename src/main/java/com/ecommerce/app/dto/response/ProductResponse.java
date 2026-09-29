package com.ecommerce.app.dto.response;

import com.ecommerce.app.entity.Product;
import com.ecommerce.app.entity.ProductImage;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class ProductResponse {
    private UUID id;
    private String title;
    private String description;
    private String categoryName;
    private String brand;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private boolean featured;
    private boolean flashSale;
    private Instant flashSaleEndTime;
    private Long secondsRemaining;   // null if not a flash sale / no end time set / already ended
    private Double avgRating;
    private Integer reviewCount;
    private List<String> images;               // general/default gallery (images not tagged to any specific color)
    private List<ProductVariantResponse> variants;  // each variant carries its OWN images — frontend swaps photos using this when color changes

    public static ProductResponse from(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setDescription(p.getDescription());
        r.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);
        r.setBrand(p.getBrand());
        r.setPrice(p.getPrice());
        r.setDiscountPrice(p.getDiscountPrice());
        r.setStock(p.getStock());
        r.setFeatured(p.isFeatured());
        r.setFlashSale(p.isFlashSale());
        r.setFlashSaleEndTime(p.getFlashSaleEndTime());
        if (p.isFlashSale() && p.getFlashSaleEndTime() != null) {
            long remaining = Duration.between(Instant.now(), p.getFlashSaleEndTime()).getSeconds();
            r.setSecondsRemaining(Math.max(remaining, 0));
        }
        r.setAvgRating(p.getAvgRating());
        r.setReviewCount(p.getReviewCount());

        // General images = ones NOT tagged to a specific variant (variant == null)
        r.setImages(p.getImages().stream()
                .filter(i -> i.getVariant() == null)
                .map(ProductImage::getUrl)
                .collect(Collectors.toList()));

        // Each variant gets its own image list (images tagged with that variant's id).
        // If a variant has no dedicated photos, frontend should fall back to the
        // general `images` list above.
        r.setVariants(p.getVariants().stream().map(v -> {
            List<String> variantImages = p.getImages().stream()
                    .filter(i -> i.getVariant() != null && i.getVariant().getId().equals(v.getId()))
                    .map(ProductImage::getUrl)
                    .collect(Collectors.toList());
            return ProductVariantResponse.from(v, variantImages);
        }).collect(Collectors.toList()));

        return r;
    }
}
