package com.example.demo_spring_project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ApiSubError {
    private String field;
    private String rejectedValue;
    private String message;

}
