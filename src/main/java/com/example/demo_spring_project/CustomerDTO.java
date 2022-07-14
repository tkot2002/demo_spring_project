package com.example.demo_spring_project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

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
        if (!addresses.isEmpty()) {
            customer.getAddresses().removeAll(customer.getAddresses());
            for (AddressDTO a : addresses) {
                customer.getAddresses().add(a.getAddressEntity(a));
            }
        }
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

    public boolean checkNull() {
        if (firstName == null || lastName == null || phoneNumber == null || emailId == null || addresses.isEmpty()) return true;
        for (AddressDTO a : addresses) {
            if (a.checkNull()) return true;
        }
        return false;
    }

    public boolean checkNullUpdate() {
        for (AddressDTO a : addresses) {
            if (a.checkNull()) return true;
        }
        return false;
    }

    public boolean checkUpdate() {
        if (firstName != null || lastName != null || phoneNumber != null) return true;
        return false;
    }
}
