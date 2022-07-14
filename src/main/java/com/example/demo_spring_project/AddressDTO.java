package com.example.demo_spring_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data public class AddressDTO {
    @JsonIgnore
    private int id;
    private String billingAccountNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String zip;
    private String state;

    Address getAddressEntity(AddressDTO addressDTO) {
        Address address = new Address();
        address.setBillingAccountNumber(addressDTO.getBillingAccountNumber());
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setCity(addressDTO.getCity());
        address.setZip(addressDTO.getZip());
        address.setState(addressDTO.getState());
        return address;
    }

    static AddressDTO getAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setBillingAccountNumber(address.getBillingAccountNumber());
        dto.setId(address.getId());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setZip(address.getZip());
        dto.setState(address.getState());
        return dto;
    }

    public boolean checkNull() {
        if (addressLine1 == null || city == null || zip == null || state == null) return true;

        return false;
    }
}
