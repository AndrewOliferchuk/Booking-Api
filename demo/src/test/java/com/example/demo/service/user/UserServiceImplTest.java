package com.example.demo.service.user;

import static com.example.demo.model.Role.RoleName.CUSTOMER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.dto.user.UserUpdateRequestDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    public static final String ENCODE_PASSWORD = "cXdlcnk=";
    public static final String EMAIL = "test@case.com";

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void currentUser_emailIsExist_Success() {
        User user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("securepassword123");

        Set<Role> roles = Set.of(new Role());
        user.setRoles(roles);

        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles.stream().map(Role::getRole).collect(Collectors.toSet())
        );

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto actual = userService.currentUser(EMAIL);

        assertThat(actual).isEqualTo(responseDto);
        verify(userRepository, times(1)).findByEmail(EMAIL);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void currentUser_emailIsNotExist_UsernameNotFoundException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.currentUser(EMAIL);
        });

        assertEquals("User not found with email: " + EMAIL, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(EMAIL);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateRole_userIdAndRoleCorrect_Success() {
        Long userId = 1L;
        Set<Role.RoleName> roles = Set.of(Role.RoleName.MANAGER, Role.RoleName.CUSTOMER);

        User user = new User();
        user.setId(userId);
        user.setEmail("asd@uo.com");
        user.setFirstName("Andrew");
        user.setLastName("Test");
        user.setRoles(roles.stream().map(roleName -> {
            Role role = new Role();
            role.setRole(roleName);
            return role;
        }).collect(Collectors.toSet()));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("asd@uo.com");
        updatedUser.setFirstName("Andrew");
        updatedUser.setLastName("Test");
        updatedUser.setRoles(roles.stream().map(roleName -> {
            Role role = new Role();
            role.setRole(roleName);
            return role;
        }).collect(Collectors.toSet()));

        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(responseDto);

        Set<Role> newRoles = roles.stream().map(roleName -> {
            Role role = new Role();
            role.setRole(roleName);
            return role;
        }).collect(Collectors.toSet());

        UserResponseDto result = userService.updateRole(userId, newRoles);

        assertEquals(responseDto, result);
        verify(userRepository).save(user);
        verify(userMapper).toDto(updatedUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void updateRole_userIsNotExist_ThrowsException() {
        Long userId = 1L;
        Set<Role> roles = Set.of();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateRole(userId, roles);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateProfile_correctValue_Success() {
        String firstName = "Alice";
        String lastName = "First";
        String password = "qwery";
        String repeatPassword = "qwery";

        User user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setFirstName("Bob");
        user.setLastName("Last");
        user.setPassword("asd");
        user.setRoles(Set.of());

        UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto(
                user.getEmail(),
                firstName,
                lastName,
                password,
                repeatPassword
        );

        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                updateRequestDto.firstName(),
                updateRequestDto.lastName(),
                Set.of(CUSTOMER)
        );

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(password)).thenReturn(ENCODE_PASSWORD);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto actual = userService.updateProfile(EMAIL,updateRequestDto);

        assertThat(actual).isEqualTo(responseDto);
        verify(userRepository).findByEmail(EMAIL);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper, passwordEncoder);
    }
}
