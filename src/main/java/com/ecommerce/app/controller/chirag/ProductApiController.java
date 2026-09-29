package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.ReviewRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.entity.Review;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.CategoryService;
import com.ecommerce.app.service.ProductService;
import com.ecommerce.app.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Module 3: Products & Catalog
 * GET /categories
 * GET /products                 (search, filter, sort, pagination)
 * GET /products/featured
 * GET /products/trending
 * GET /products/flash-sale
 * GET /products/:id
 * GET /products/:id/reviews
 * POST /products/:id/reviews
 */
@RestController
@RequiredArgsConstructor
public class ProductApiController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @GetMapping("/categories")
    public ApiResponse<List<Category>> getCategories() {
        return ApiResponse.ok(categoryService.listActive());
    }

    @GetMapping("/products")
    public ApiResponse<PageResponse<?>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String query,
            Pageable pageable) {
        if (query != null && !query.isBlank()) {
            return ApiResponse.ok(productService.search(query, pageable));
        }
        return ApiResponse.ok(productService.listProducts(category, brand, minPrice, maxPrice, sort, pageable));
    }

    @GetMapping("/products/featured")
    public ApiResponse<?> getFeatured() {
        return ApiResponse.ok(productService.getFeatured());
    }

    @GetMapping("/products/trending")
    public ApiResponse<?> getTrending(@AuthenticationPrincipal UserPrincipal principal) {
        // "Trending" mapped to the recommendation feed (newest/most-relevant active products).
        return ApiResponse.ok(productService.getRecommended(principal != null ? principal.getId() : null));
    }

    @GetMapping("/products/flash-sale")
    public ApiResponse<?> getFlashSale() {
        return ApiResponse.ok(productService.getFlashSale());
    }

    @GetMapping("/products/{id}")
    public ApiResponse<?> getProductDetail(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(productService.getProduct(id, principal != null ? principal.getId() : null));
    }

    @GetMapping("/products/{id}/reviews")
    public ApiResponse<PageResponse<Review>> getReviews(@PathVariable UUID id, Pageable pageable) {
        return ApiResponse.ok(reviewService.listByProduct(id, pageable));
    }

    @PostMapping("/products/{id}/reviews")
    public ApiResponse<Review> addReview(@PathVariable UUID id, @Valid @RequestBody ReviewRequest request,
                                          @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok("Review added", reviewService.addReview(principal.getId(), id, request));
    }
}
