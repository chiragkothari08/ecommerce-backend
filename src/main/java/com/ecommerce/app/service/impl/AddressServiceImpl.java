package com.ecommerce.app.service.impl;

import com.ecommerce.app.dto.request.AddressRequest;
import com.ecommerce.app.dto.response.AddressResponse;
import com.ecommerce.app.entity.Address;
import com.ecommerce.app.entity.User;
import com.ecommerce.app.exception.ApiException;
import com.ecommerce.app.repository.AddressRepository;
import com.ecommerce.app.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public List<AddressResponse> list(UUID userId) {
        return addressRepository.findByUserId(userId).stream().map(AddressResponse::from).collect(Collectors.toList());
    }

    @Override
    public AddressResponse get(UUID userId, UUID addressId) {
        return AddressResponse.from(findOwned(userId, addressId));
    }

    @Override
    @Transactional
    public AddressResponse create(UUID userId, AddressRequest request) {
        Address address = new Address();
        User u = new User();
        u.setId(userId);
        address.setUser(u);
        apply(address, request);

        if (request.isDefault()) {
            unsetOtherDefaults(userId);
        }
        return AddressResponse.from(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse update(UUID userId, UUID addressId, AddressRequest request) {
        Address address = findOwned(userId, addressId);
        apply(address, request);
        if (request.isDefault()) {
            unsetOtherDefaults(userId);
            address.setDefault(true);
        }
        return AddressResponse.from(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID addressId) {
        Address address = findOwned(userId, addressId);
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void setDefault(UUID userId, UUID addressId) {
        unsetOtherDefaults(userId);
        Address address = findOwned(userId, addressId);
        address.setDefault(true);
        addressRepository.save(address);
    }

    private Address findOwned(UUID userId, UUID addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> ApiException.notFound("Address not found"));
        if (!address.getUser().getId().equals(userId)) throw ApiException.forbidden("Address does not belong to this user");
        return address;
    }

    private void unsetOtherDefaults(UUID userId) {
        addressRepository.findByUserId(userId).forEach(a -> {
            if (a.isDefault()) {
                a.setDefault(false);
                addressRepository.save(a);
            }
        });
    }

    private void apply(Address address, AddressRequest request) {
        address.setLabel(request.getLabel());
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
    }
}
