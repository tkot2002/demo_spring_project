package com.example.demo_spring_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DemoSpringProjectApplication{

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringProjectApplication.class, args);
    }
}
