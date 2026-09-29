package com.ecommerce.app.service;

import com.ecommerce.app.dto.request.AddressRequest;
import com.ecommerce.app.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    List<AddressResponse> list(UUID userId);
    AddressResponse get(UUID userId, UUID addressId);
    AddressResponse create(UUID userId, AddressRequest request);
    AddressResponse update(UUID userId, UUID addressId, AddressRequest request);
    void delete(UUID userId, UUID addressId);
    void setDefault(UUID userId, UUID addressId);
}
