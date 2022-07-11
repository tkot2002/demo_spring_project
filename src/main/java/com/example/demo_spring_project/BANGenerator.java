package com.example.demo_spring_project;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BANGenerator {

    @Autowired
    CustomerRepository customerRepository;

    public String generate() {
        Random random = new Random();
        boolean duplicate = true;
        String ban = "";

        while(duplicate) {
            ban = String.format("%09d", random.nextInt(1000000000));
            duplicate = customerRepository.existsByBillingAccountNumber(ban);
        }
        return ban;
    }
}