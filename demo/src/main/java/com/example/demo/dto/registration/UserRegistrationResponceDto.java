package com.example.demo.dto.registration;

public record UserRegistrationResponceDto(
        Long id,
        String email,
        String firstName,
        String lastName
) {
}
