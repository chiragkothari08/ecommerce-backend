package com.ecommerce.app.dto.response;

import com.ecommerce.app.entity.ProductVariant;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ProductVariantResponse {
    private UUID id;
    private String size;
    private String color;
    private BigDecimal priceOverride;
    private Integer stock;
    private List<String> images;   // photos specific to this color/size — empty if none tagged

    public static ProductVariantResponse from(ProductVariant v, List<String> images) {
        ProductVariantResponse r = new ProductVariantResponse();
        r.setId(v.getId());
        r.setSize(v.getSize());
        r.setColor(v.getColor());
        r.setPriceOverride(v.getPriceOverride());
        r.setStock(v.getStock());
        r.setImages(images);
        return r;
    }
}
