package com.example.demo.dto.registration;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Email
        @Length(min = 8, max = 16)
        String email,
        @NotBlank
        @Length(min = 8, max = 16)
        String password
) {
}
