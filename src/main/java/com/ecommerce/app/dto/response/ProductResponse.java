package com.ecommerce.app.dto.response;

import com.ecommerce.app.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private Double avgRating;
    private Integer reviewCount;
    private List<String> images;

    public static ProductResponse from(Product p) {
        if (p == null) return null;
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setDescription(p.getDescription());
        
        try {
            r.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : "Fashion");
        } catch (Exception ex) {
            r.setCategoryName("Fashion");
        }

        r.setBrand(p.getBrand());
        r.setPrice(p.getPrice());
        r.setDiscountPrice(p.getDiscountPrice());
        r.setStock(p.getStock());
        r.setFeatured(p.isFeatured());
        r.setFlashSale(p.isFlashSale());
        r.setAvgRating(p.getAvgRating());
        r.setReviewCount(p.getReviewCount());

        try {
            if (p.getImages() != null && !p.getImages().isEmpty()) {
                r.setImages(p.getImages().stream()
                        .map(i -> i.getUrl())
                        .filter(u -> u != null && !u.isBlank())
                        .collect(Collectors.toList()));
            } else {
                r.setImages(new ArrayList<>());
            }
        } catch (Exception ex) {
            r.setImages(new ArrayList<>());
        }

        return r;
    }
}
