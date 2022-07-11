package com.example.demo_spring_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomerDTO {
    private String billingAccountNumber;
    private String firstName;
    private String lastName;
    private List<AddressDTO> addresses = new ArrayList<>();
    private String phoneNumber;
    private String emailId;

    Customer getCustomerEntity(CustomerDTO customerDTO){
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        for (AddressDTO a : customerDTO.getAddresses()) {
            customer.getAddresses().add(a.getAddressEntity(a));
        }
        customer.setPhoneNumber(phoneNumber);
        customer.setEmailId(emailId);
        return customer;
    }

    void updateFromDTO(Customer customer) {
        if (firstName != null) customer.setFirstName(firstName);
        if (lastName != null) customer.setLastName(lastName);
        if (!addresses.isEmpty()) {
            customer.getAddresses().removeAll(customer.getAddresses());
            for (AddressDTO a : addresses) {
                customer.getAddresses().add(a.getAddressEntity(a));
            }
        }
        if (phoneNumber != null) customer.setPhoneNumber(phoneNumber);
        if (emailId != null) customer.setEmailId(emailId);
    }
    static CustomerDTO getCustomerDTO(Customer customer){
        CustomerDTO dto = new CustomerDTO();
        dto.setBillingAccountNumber(customer.getBillingAccountNumber());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        for (Address a : customer.getAddresses()) {
            dto.getAddresses().add(AddressDTO.getAddressDTO(a));
        }
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setEmailId(customer.getEmailId());
        return dto;
    }
}
