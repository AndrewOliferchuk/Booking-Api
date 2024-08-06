package com.example.demo.dto.user;

import com.example.demo.model.Role;
import java.util.Set;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<Role.RoleName> roles
) {
}
