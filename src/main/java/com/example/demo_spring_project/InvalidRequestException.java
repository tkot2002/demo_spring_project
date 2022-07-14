package com.example.demo_spring_project;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InvalidRequestException extends Exception {
    private List<ApiSubError> subErrors = new ArrayList<>();

    public InvalidRequestException(String errorMessage) {
        super(errorMessage);
    }
}
