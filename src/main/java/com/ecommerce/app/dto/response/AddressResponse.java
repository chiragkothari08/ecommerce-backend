package com.ecommerce.app.dto.response;

import com.ecommerce.app.entity.Address;
import lombok.Data;

import java.util.UUID;

@Data
public class AddressResponse {
    private UUID id;
    private String label;
    private String fullName;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private boolean isDefault;

    public static AddressResponse from(Address a) {
        AddressResponse r = new AddressResponse();
        r.setId(a.getId());
        r.setLabel(a.getLabel());
        r.setFullName(a.getFullName());
        r.setPhone(a.getPhone());
        r.setAddressLine1(a.getAddressLine1());
        r.setAddressLine2(a.getAddressLine2());
        r.setCity(a.getCity());
        r.setState(a.getState());
        r.setZipCode(a.getZipCode());
        r.setCountry(a.getCountry());
        r.setDefault(a.isDefault());
        return r;
    }
}
