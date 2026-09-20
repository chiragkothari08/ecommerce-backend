package com.ecommerce.app.controller;

import com.ecommerce.app.dto.request.ReviewRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Review;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.ProductService;
import com.ecommerce.app.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    @GetMapping("/products")
    public ApiResponse<PageResponse<?>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sort,
            Pageable pageable) {
        return ApiResponse.ok(productService.listProducts(category, brand, minPrice, maxPrice, sort, pageable));
    }

    @GetMapping("/products/{id}")
    public ApiResponse<?> get(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(productService.getProduct(id, principal != null ? principal.getId() : null));
    }

    @GetMapping("/products/{id}/variants")
    public ApiResponse<?> variants(@PathVariable UUID id) {
        return ApiResponse.ok(productService.getProduct(id, null));
    }

    @GetMapping("/products/{id}/reviews")
    public ApiResponse<PageResponse<Review>> reviews(@PathVariable UUID id, Pageable pageable) {
        return ApiResponse.ok(reviewService.listByProduct(id, pageable));
    }

    @PostMapping("/products/{id}/reviews")
    public ApiResponse<Review> addReview(@PathVariable UUID id, @Valid @RequestBody ReviewRequest request,
                                          @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Review added", reviewService.addReview(principal.getId(), id, request));
    }

    @GetMapping("/products/{id}/related")
    public ApiResponse<List<?>> related(@PathVariable UUID id) {
        return ApiResponse.ok(productService.getRelated(id));
    }

    @GetMapping("/products/search")
    public ApiResponse<PageResponse<?>> search(@RequestParam String query, Pageable pageable) {
        return ApiResponse.ok(productService.search(query, pageable));
    }

    @GetMapping("/search/suggestions")
    public ApiResponse<List<String>> suggestions(@RequestParam String query) {
        // Wire up to a lightweight prefix/typeahead index (e.g. Redis sorted sets or Elasticsearch) for production use.
        return ApiResponse.ok(List.of());
    }

    @GetMapping("/search/filters")
    public ApiResponse<Map<String, Object>> filters() {
        return ApiResponse.ok(Map.of(
                "priceRange", Map.of("min", 0, "max", 100000),
                "sortOptions", List.of("newest", "price_low_high", "price_high_low", "rating")
        ));
    }
}
