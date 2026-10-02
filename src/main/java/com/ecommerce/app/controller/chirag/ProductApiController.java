package com.ecommerce.app.controller.chirag;

import com.ecommerce.app.dto.request.ReviewRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.dto.response.PageResponse;
import com.ecommerce.app.dto.response.ReviewResponse;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.CategoryService;
import com.ecommerce.app.service.ProductService;
import com.ecommerce.app.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sorting = Sort.unsorted();

        if (sort != null) {
            sort = sort.trim();

            if (!sort.isBlank()
                    && !sort.equals("[]")
                    && !sort.equals("[\"string\"]")
                    && !sort.equalsIgnoreCase("string")) {

                String[] sortParts = sort.split(",");

                String field = sortParts[0].trim();
                String direction = "asc";

                if (sortParts.length > 1) {
                    direction = sortParts[1].trim();
                }

                if (isValidSortField(field)) {
                    Sort.Direction sortDirection =
                            "desc".equalsIgnoreCase(direction)
                                    ? Sort.Direction.DESC
                                    : Sort.Direction.ASC;

                    sorting = Sort.by(sortDirection, field);
                }
            }
        }

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Pageable pageable = PageRequest.of(page, size, sorting);

        if (query != null && !query.isBlank()) {
            return ApiResponse.ok(
                    productService.search(query, pageable)
            );
        }

        return ApiResponse.ok(
                productService.listProducts(
                        category,
                        brand,
                        minPrice,
                        maxPrice,
                        sort,
                        pageable
                )
        );
    }

    private boolean isValidSortField(String field) {
        return field.equals("price")
                || field.equals("title")
                || field.equals("brand")
                || field.equals("createdAt")
                || field.equals("avgRating")
                || field.equals("reviewCount")
                || field.equals("stock");
    }

    @GetMapping("/products/featured")
    public ApiResponse<?> getFeatured() {
        return ApiResponse.ok(productService.getFeatured());
    }

    @GetMapping("/products/trending")
    public ApiResponse<?> getTrending(
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.ok(
                productService.getRecommended(
                        principal != null ? principal.getId() : null
                )
        );
    }

    @GetMapping("/products/flash-sale")
    public ApiResponse<?> getFlashSale() {
        return ApiResponse.ok(productService.getFlashSale());
    }

    @GetMapping("/products/{id}")
    public ApiResponse<?> getProductDetail(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.ok(
                productService.getProduct(
                        id,
                        principal != null ? principal.getId() : null
                )
        );
    }

    @GetMapping("/products/{id}/reviews")
    public ApiResponse<PageResponse<ReviewResponse>> getReviews(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        if (size > 100) {
            size = 100;
        }

        Pageable pageable = PageRequest.of(page, size);

        return ApiResponse.ok(
                reviewService.listByProduct(id, pageable)
        );
    }

    @PostMapping("/products/{id}/reviews")
    public ApiResponse<ReviewResponse> addReview(
            @PathVariable UUID id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        return ApiResponse.ok(
                "Review added",
                reviewService.addReview(
                        principal.getId(),
                        id,
                        request
                )
        );
    }
}
