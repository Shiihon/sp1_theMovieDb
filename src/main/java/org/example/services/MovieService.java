package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MovieService {

    private final ObjectMapper objectMapper;

    public MovieService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
