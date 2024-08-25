package com.example.demo.dto.user;

import com.example.demo.model.Role;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UserUpdateRolesDto(
        @NotEmpty
        Set<Role> roles
) {
}
