package com.ecommerce.app.controller;

import com.ecommerce.app.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LocationController {

    @Value("${app.google.maps-api-key}")
    private String mapsApiKey;

    private final RestClient restClient = RestClient.create();

    @GetMapping("/api/locations/geocode")
    public ApiResponse<Map<String, Object>> geocode(@RequestParam String address) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + mapsApiKey;
        Map<?, ?> result = restClient.get().uri(url).retrieve().body(Map.class);
        return ApiResponse.ok((Map<String, Object>) result);
    }

    @GetMapping("/api/locations/reverse-geocode")
    public ApiResponse<Map<String, Object>> reverseGeocode(@RequestParam double lat, @RequestParam double lng) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + mapsApiKey;
        Map<?, ?> result = restClient.get().uri(url).retrieve().body(Map.class);
        return ApiResponse.ok((Map<String, Object>) result);
    }

    @GetMapping("/api/delivery/estimate")
    public ApiResponse<Map<String, Object>> deliveryEstimate(@RequestParam double lat, @RequestParam double lng) {
        // Combine distance from nearest warehouse (Google Distance Matrix API) with your
        // own SLA rules to compute a real ETA. Placeholder below.
        return ApiResponse.ok(Map.of("estimatedDays", 4, "expressAvailable", true));
    }
}
