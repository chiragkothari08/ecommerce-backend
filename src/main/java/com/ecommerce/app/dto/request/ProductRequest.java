package com.ecommerce.app.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
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
    private Instant flashSaleEndTime;   // e.g. "2026-09-10T18:30:00Z" — when the flash sale ends
}
