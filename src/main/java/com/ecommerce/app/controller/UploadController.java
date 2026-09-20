package com.ecommerce.app.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.app.dto.response.ApiResponse;
import com.ecommerce.app.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final Cloudinary cloudinary;

    @PostMapping("/image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(upload(file, "ecommerce/misc"));
    }

    @PostMapping("/product-images")
    public ApiResponse<List<Map<String, String>>> uploadProductImages(@RequestParam("files") List<MultipartFile> files) {
        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(upload(file, "ecommerce/products"));
        }
        return ApiResponse.ok(results);
    }

    @DeleteMapping("/{publicId}")
    public ApiResponse<Void> delete(@PathVariable String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return ApiResponse.ok("Image deleted", null);
        } catch (IOException e) {
            throw ApiException.badRequest("Failed to delete image: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> upload(MultipartFile file, String folder) {
        try {
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", folder));
            return Map.of(
                    "url", (String) result.get("secure_url"),
                    "publicId", (String) result.get("public_id")
            );
        } catch (IOException e) {
            throw ApiException.badRequest("Upload failed: " + e.getMessage());
        }
    }
}
