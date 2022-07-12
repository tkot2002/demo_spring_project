package com.example.demo_spring_project;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "billingAccountNumber", scope = String.class)
@Entity
@Table(name = "customer")
@Data public class Customer {

    @Id
    @Column(name = "billing_account_number")
    private String billingAccountNumber;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    //@ToString.Exclude
    //@JsonManagedReference
    private List<Address> addresses = new ArrayList<>();
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "email_id")
    private String emailId;
}