package com.example.demo.controller;

import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.dto.user.UserUpdateRequestDto;
import com.example.demo.dto.user.UserUpdateRolesDto;
import com.example.demo.model.User;
import com.example.demo.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users management",
        description = "Endpoints for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get the profile of the currently authenticated user",
            description = "Retrieves the user profile information for the "
                    + "currently authenticated user")
    public UserResponseDto getUser(@AuthenticationPrincipal User user) {
        return userService.currentUser(user.getEmail());
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Update roles for a specific user",
            description = "Assigns new roles to a user identified by the user ID")
    public UserResponseDto updateRoleForUser(@PathVariable Long id,
             @RequestBody @Valid UserUpdateRolesDto requestDto) {
        return userService.updateRole(id, requestDto.roles());
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update the profile of the currently authenticated user",
               description = "Allows the currently authenticated user to update their profile")
    public UserResponseDto updateProfile(@AuthenticationPrincipal User user,
                            @RequestBody @Valid UserUpdateRequestDto requestDto) {
        return userService.updateProfile(user.getEmail(), requestDto);
    }
}
