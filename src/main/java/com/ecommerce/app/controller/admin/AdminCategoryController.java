package com.ecommerce.app.controller.admin;

import com.ecommerce.app.dto.request.CategoryRequest;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.entity.Category;
import com.ecommerce.app.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse<Map<String, UUID>> create(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.ok("Category created", Map.of("id", categoryService.create(request)));
    }

    @GetMapping
    public ApiResponse<List<Category>> list() {
        return ApiResponse.ok(categoryService.listActive());
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        categoryService.update(id, request);
        return ApiResponse.ok("Category updated", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ApiResponse.ok("Category deleted", null);
    }
}
