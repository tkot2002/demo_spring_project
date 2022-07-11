package com.example.demo_spring_project;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.ToString;

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Integer.class)
@Entity
@Table(name = "address")
@Data public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_number", referencedColumnName = "billing_account_number")
    @JsonBackReference
    @ToString.Exclude
    private Customer customer;
    @Column(name = "address_line1")
    private String addressLine1;
    @Column(name = "address_line2")
    private String addressLine2;
    @Column(name = "city")
    private String city;
    @Column(name = "zip")
    private String zip;
    @Column(name = "state")
    private String state;
}
