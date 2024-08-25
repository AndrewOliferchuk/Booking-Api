package com.example.demo.service.user;

import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.dto.user.UserUpdateRequestDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto currentUser(String email) {
        User user = findUserByEmail(email);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateRole(Long userId, Set<Role> roles) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        user.setRoles(roles);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateProfile(String email, UserUpdateRequestDto requestDto) {
        User user = findUserByEmail(email);
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        return userMapper.toDto(userRepository.save(user));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
