package com.example.demo.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto(
        @NotBlank
        String email,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String password,
        @NotBlank
        String repeatPassword
) {
}
