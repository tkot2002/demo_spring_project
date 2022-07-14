package com.example.demo_spring_project;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    public static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerRepository customerRepo;
    private final BANGenerator banGenerator;

    @Value("#{${stateList}}")
    public HashMap<String,String> stateMap;


    @Operation(summary = "Creates a customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created.", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Bad request. Zip and phone number must be 5 and 10 digits respectively. State must be a US state or its 2-letter code."),
            @ApiResponse(responseCode = "404", description = "Customer not found.")
    })
    @PostMapping("/customer")
    public ResponseEntity<String> createCustomer(@RequestHeader(value = "xConversationId",required = false) String xConversationId, @RequestBody CustomerDTO dto) throws InvalidRequestException {
        InvalidRequestException ire = new InvalidRequestException("Validation Errors:");
        if (dto.checkNull()) throw new InvalidRequestException("One or more required fields are null.");

        if (!isNumeric(dto.getPhoneNumber()) || dto.getPhoneNumber().length() != 10) {
            ire.getSubErrors().add(new ApiSubError("Phone Number", dto.getPhoneNumber(),"Phone number must be a 10-digit string."));
        }
        for (AddressDTO a : dto.getAddresses()) {

            if (!isNumeric(a.getZip()) || a.getZip().length() != 5) {
                ire.getSubErrors().add(new ApiSubError("Zip", a.getZip(),"Zip must be a 5-digit string."));
            }
            if (checkState(a.getState()) == null) {
                ire.getSubErrors().add(new ApiSubError("State", a.getState(),"Either enter a US state or its 2-letter code."));
            }
            a.setState(checkState(a.getState()));
        }
        if (!ire.getSubErrors().isEmpty()) {
            throw ire;
        }

        Customer customer = dto.getCustomerEntity(dto);
        customer.setBillingAccountNumber(banGenerator.generate());
        for (Address a : customer.getAddresses()) {
            a.setBillingAccountNumber(customer.getBillingAccountNumber());
        }
        xConversationId = xConversationId == null ? UUID.randomUUID().toString() : xConversationId;
        LOGGER.info("Get customer call being made for Billing Account Number: {} with a ConversationId: {}",customer.getBillingAccountNumber(),xConversationId);
        customerRepo.save(customer);
        return new ResponseEntity<>("BillingAccountNumber: " + customer.getBillingAccountNumber(), HttpStatus.OK);
    }

    @Operation(summary = "Updates a customer from their billingAccountNumber")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully updated."),
            @ApiResponse(responseCode = "400", description = "Bad request. Zip must be 5 digits. Only address and email can be modified. State must be a US state or its 2-digit code."),
            @ApiResponse(responseCode = "404", description = "Customer not found.")
    })
    @PutMapping("/customer/{ban}")
    public ResponseEntity<String> updateCustomer(@RequestHeader(value = "xConversationId",required = false) String xConversationId, @PathVariable String ban, @RequestBody CustomerDTO dto) throws InvalidRequestException, NotFoundException {
        xConversationId = xConversationId == null ? UUID.randomUUID().toString() : xConversationId;
        LOGGER.info("Update customer call being made for Billing Account Number: {} with a ConversationId: {}",ban,xConversationId);

        List<Customer> customers = customerRepo.findCustomerByBillingAccountNumber(ban);
        if(customers.isEmpty()) {
            throw new NotFoundException("Customer not found.");
        }

        InvalidRequestException ire = new InvalidRequestException("Validation Errors:");
        if (dto.checkUpdate()) throw new InvalidRequestException("First name, last name, and phone number cannot be changed.");
        if (dto.checkNullUpdate()) throw new InvalidRequestException("One or more required fields are null.");

        for (AddressDTO a : dto.getAddresses()) {
            if ((a.getZip() != null) &&!isNumeric(a.getZip()) || a.getZip().length() != 5) {
                ire.getSubErrors().add(new ApiSubError("Zip", a.getZip(),"Zip must be a 5-digit string."));
            }
            if ((a.getState() != null) && checkState(a.getState()) == null) {
                ire.getSubErrors().add(new ApiSubError("State", a.getState(),"Either enter a US state or its 2-letter code."));
            }
            a.setState(checkState(a.getState()));
        }
        if (!ire.getSubErrors().isEmpty()) {
            throw ire;
        }
        dto.updateFromDTO(customers.get(0));
        for (Address a : customers.get(0).getAddresses()) {
            a.setBillingAccountNumber(customers.get(0).getBillingAccountNumber());
        }
        customerRepo.save(customers.get(0));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Retrieves a customer from their billingAccountNumber")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Customer not found.")
    })
    @GetMapping("/customer/{ban}")
    public ResponseEntity<CustomerDTO> getCustomer(@RequestHeader(value = "xConversationId",required = false) String xConversationId, @PathVariable String ban) throws NotFoundException {
        xConversationId = xConversationId == null ? UUID.randomUUID().toString() : xConversationId;
        LOGGER.info("Get customer call being made for Billing Account Number: {} with a ConversationId: {}",ban,xConversationId);
        List<Customer> customers = customerRepo.findCustomerByBillingAccountNumber(ban);
        if (customers.isEmpty()) {
            throw new NotFoundException("Customer not found.");
        }
        else {
            CustomerDTO dto = CustomerDTO.getCustomerDTO(customers.get(0));
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    @Operation(summary = "Deletes a customer from their billingAccountNumber")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted."),
            @ApiResponse(responseCode = "404", description = "Customer not found.")
    })
    @Transactional
    @DeleteMapping("/customer/{ban}")
    public ResponseEntity<HttpStatus> deleteCustomer(@RequestHeader(value = "xConversationId",required = false) String xConversationId, @PathVariable String ban) throws NotFoundException{
        xConversationId = xConversationId == null ? UUID.randomUUID().toString() : xConversationId;
        LOGGER.info("Delete customer call being made for Billing Account Number: {} with a ConversationId: {}",ban,xConversationId);


        long deleted = customerRepo.deleteByBillingAccountNumber(ban);
        if (deleted == 0) {
            throw new NotFoundException("Customer not found.");
        }
        else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    public static boolean isNumeric(String s) {
        try {
            Long.parseLong(s);
        } catch (NumberFormatException e) {
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

