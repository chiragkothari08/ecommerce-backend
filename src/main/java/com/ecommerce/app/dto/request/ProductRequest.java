package com.ecommerce.app.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequest {
    private String title;
    private String description;
    private UUID categoryId;
    private String brand;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private boolean featured;
    private boolean flashSale;
}
