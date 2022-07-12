package com.example.demo_spring_project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>{
    List<Customer> findCustomerByBillingAccountNumber(String billingAccountNumber);
    long deleteByBillingAccountNumber(String billingAccountNumber);
    boolean existsByBillingAccountNumber(String ban);

}