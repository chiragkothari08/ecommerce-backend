package com.ecommerce.app.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CategoryRequest {
    private String name;
    private String slug;
    private String imageUrl;
    private UUID parentId;
    private int sortOrder;
}
