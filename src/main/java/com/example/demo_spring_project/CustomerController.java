package com.example.demo_spring_project;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import javax.transaction.Transactional;

@RestController
public class CustomerController {
    public static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
    @Autowired
    CustomerRepository customerRepo;
    @Autowired
    BANGenerator banGenerator;
    @Value("#{${stateList}}")
    public HashMap<String,String> stateMap;

    @PostMapping("/customer/new")
    public ResponseEntity<String> createCustomer(@RequestHeader(value = "xConversationId",required = false) String xConversationId, @RequestBody CustomerDTO dto) {


        if (!isNumeric(dto.getPhoneNumber()) || dto.getPhoneNumber().length() != 10) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        for (AddressDTO a : dto.getAddresses()) {
            if (!isNumeric(a.getZip()) || a.getZip().length() != 5) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if (checkState(a.getState()) == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            a.setState(checkState(a.getState()));
        }
        Customer customer = dto.getCustomerEntity(dto);
        customer.setBillingAccountNumber(banGenerator.generate());
        for (Address a : customer.getAddresses()) {
            //a.setBillingAccountNumber(customer.getBillingAccountNumber());
            a.setCustomer(customer);
        }
        xConversationId = xConversationId == null ? UUID.randomUUID().toString() : xConversationId;
        LOGGER.info("Get customer call being made for Billing Account Number: {} with a ConversationId: {}",customer.getBillingAccountNumber(),xConversationId);
        customerRepo.save(customer);
        return new ResponseEntity<>(customer.getBillingAccountNumber(), HttpStatus.OK);
    }

    @PutMapping("/customer/{ban}")
    public ResponseEntity<String> updateCustomer(@RequestHeader(value = "xConversationId",required = false) String xConversationId, @PathVariable String ban, @RequestBody CustomerDTO dto) {
        xConversationId = xConversationId == null ? UUID.randomUUID().toString() : xConversationId;
        LOGGER.info("Update customer call being made for Billing Account Number: {} with a ConversationId: {}",ban,xConversationId);

        List<Customer> customers = customerRepo.findCustomerByBillingAccountNumber(ban);
        if(customers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        for (AddressDTO a : dto.getAddresses()) {
            if ((a.getZip() != null) &&!isNumeric(a.getZip()) || a.getZip().length() != 5) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else if ((a.getState() != null) && checkState(a.getState()) == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            a.setState(checkState(a.getState()));
        }
        dto.updateFromDTO(customers.get(0));
        for (Address a : customers.get(0).getAddresses()) {
            //a.setBillingAccountNumber(customers.get(0).getBillingAccountNumber());
            a.setCustomer(customers.get(0));
        }
        customerRepo.save(customers.get(0));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/customer/{ban}")
    public ResponseEntity<CustomerDTO> getCustomer(@RequestHeader(value = "xConversationId",required = false) String xConversationId, @PathVariable String ban) {
        xConversationId = xConversationId == null ? UUID.randomUUID().toString() : xConversationId;
        LOGGER.info("Get customer call being made for Billing Account Number: {} with a ConversationId: {}",ban,xConversationId);
        List<Customer> customers = customerRepo.findCustomerByBillingAccountNumber(ban);
        if (customers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            CustomerDTO dto = CustomerDTO.getCustomerDTO(customers.get(0));
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    @Transactional
    @DeleteMapping("/customer/{ban}")
    public ResponseEntity<HttpStatus> deleteCustomer(@RequestHeader(value = "xConversationId",required = false) String xConversationId, @PathVariable String ban) {
        xConversationId = xConversationId == null ? UUID.randomUUID().toString() : xConversationId;
        LOGGER.info("Delete customer call being made for Billing Account Number: {} with a ConversationId: {}",ban,xConversationId);
        long deleted = customerRepo.deleteByBillingAccountNumber(ban);
        if (deleted == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    public static boolean isNumeric(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }

    public String checkState(String s) {
        if (s.length() < 2) {
            return null;
        }
        s = s.toLowerCase();
        if (stateMap.containsKey(s)) {
            return stateMap.get(s);
        } else if (stateMap.containsValue(s)) {
            return s;
        }
        return null;
    }
}

