package com.example.demo.service.user;

import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.dto.user.UserUpdateRequestDto;
import com.example.demo.model.Role;
import java.util.Set;

public interface UserService {
    UserResponseDto currentUser(String email);

    UserResponseDto updateRole(Long userId, Set<Role> roles);

    UserResponseDto updateProfile(String email, UserUpdateRequestDto requestDto);
}
