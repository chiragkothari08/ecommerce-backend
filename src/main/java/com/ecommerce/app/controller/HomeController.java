package com.ecommerce.app.controller;

import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.security.UserPrincipal;
import com.ecommerce.app.service.CategoryService;
import com.ecommerce.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/api/home")
    public ApiResponse<Map<String, Object>> home(@AuthenticationPrincipal UserPrincipal principal) {
        var userId = principal != null ? principal.getId() : null;
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("categories", categoryService.listActive());
        data.put("featuredProducts", productService.getFeatured());
        data.put("flashSaleProducts", productService.getFlashSale());
        data.put("recommendedProducts", productService.getRecommended(userId));
        if (userId != null) {
            data.put("recentlyViewed", productService.getRecentlyViewed(userId));
        }
        return ApiResponse.ok(data);
    }

    @GetMapping("/api/banners")
    public ApiResponse<List<Map<String, String>>> banners() {
        // Static/promotional content — wire to a CMS or a "banners" table if needed.
        return ApiResponse.ok(List.of());
    }

    @GetMapping("/api/categories")
    public ApiResponse<List<Category>> categories() {
        return ApiResponse.ok(categoryService.listActive());
    }

    @GetMapping("/api/products/featured")
    public ApiResponse<?> featured() {
        return ApiResponse.ok(productService.getFeatured());
    }

    @GetMapping("/api/products/flash-sale")
    public ApiResponse<?> flashSale() {
        return ApiResponse.ok(productService.getFlashSale());
    }

    @GetMapping("/api/products/recommended")
    public ApiResponse<?> recommended(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(productService.getRecommended(principal != null ? principal.getId() : null));
    }

    @GetMapping("/api/products/recently-viewed")
    public ApiResponse<?> recentlyViewed(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(productService.getRecentlyViewed(principal.getId()));
    }
}
