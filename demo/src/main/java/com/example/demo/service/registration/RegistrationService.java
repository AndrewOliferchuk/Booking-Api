package com.example.demo.service.registration;

import com.example.demo.dto.registration.UserRegistrationRequestDto;
import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.exception.RegistrationException;

public interface RegistrationService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
