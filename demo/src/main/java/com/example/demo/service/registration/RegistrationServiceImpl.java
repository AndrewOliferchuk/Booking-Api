package com.example.demo.service.registration;

import com.example.demo.dto.registration.UserRegistrationRequestDto;
import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.exception.RegistrationException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        String email = requestDto.email();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("User with email " + email + " already exists");
        }
        User user = userMapper.toModel(requestDto);
        String encryptedPassword = passwordEncoder.encode(requestDto.password());
        user.setPassword(encryptedPassword);
        user.setRoles(Set.of(roleRepository.findByRole(Role.RoleName.CUSTOMER).orElseThrow(
                () -> new RegistrationException("Can't find role CUSTOMER")
        )));
        return userMapper.toDto(userRepository.save(user));
    }
}
