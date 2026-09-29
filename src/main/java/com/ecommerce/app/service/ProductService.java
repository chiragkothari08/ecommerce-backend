package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.ProductRequest;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.dto.response.ProductResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    PageResponse<ProductResponse> listProducts(String category, String brand, Double minPrice, Double maxPrice, String sort, Pageable pageable);
    ProductResponse getProduct(UUID id, UUID userId);
    List<ProductResponse> getFeatured();
    List<ProductResponse> getFlashSale();
    List<ProductResponse> getRecommended(UUID userId);
    List<ProductResponse> getRecentlyViewed(UUID userId);
    List<ProductResponse> getRelated(UUID productId);
    PageResponse<ProductResponse> search(String query, Pageable pageable);
    UUID createProduct(ProductRequest request);
    void updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id);
}
