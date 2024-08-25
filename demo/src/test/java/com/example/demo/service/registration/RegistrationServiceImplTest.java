package com.example.demo.service.registration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.dto.registration.UserRegistrationRequestDto;
import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.exception.RegistrationException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {
    public static final String EMAIL = "test@case.com";
    public static final String ENCRYPTED_PASSWORD = "encryptedPassword";

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void register_allCorrectValue_Success() throws RegistrationException {
        Role customerRole = new Role();
        customerRole.setRole(Role.RoleName.CUSTOMER);

        User user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);
        user.setPassword(ENCRYPTED_PASSWORD);
        user.setFirstName("test");
        user.setLastName("last");

        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(EMAIL,
                "password",
                "password",
                "John",
                "Doe");

        Set<Role.RoleName> roles = Set.of(Role.RoleName.MANAGER, Role.RoleName.CUSTOMER);
        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );

        String password = "password";
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(roleRepository.findByRole(Role.RoleName.CUSTOMER)).thenReturn(Optional.of(
                customerRole));
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(password)).thenReturn(ENCRYPTED_PASSWORD);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto actual = registrationService.register(requestDto);

        assertThat(actual).isEqualTo(responseDto);
        verify(userRepository,times(1)).existsByEmail(EMAIL);
        verify(roleRepository,times(1)).findByRole(Role.RoleName.CUSTOMER);
        verify(userMapper,times(1)).toModel(requestDto);
        verify(passwordEncoder).encode(password);
        verify(userRepository,times(1)).save(user);
        verify(userMapper,times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, userMapper, roleRepository, passwordEncoder);
    }

    @Test
    void register_emailIsExist_RegistrationException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(EMAIL,
                "password",
                "password",
                "John",
                "Doe");

        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        RegistrationException exception = assertThrows(RegistrationException.class, () -> {
            registrationService.register(requestDto);
        });

        assertEquals(exception.getMessage(), "User with email " + EMAIL
                + " already exists");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_notFoundRoles_throwsRegistrationException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(EMAIL,
                "password",
                "password",
                "John",
                "Doe");

        User user = new User();
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(roleRepository.findByRole(Role.RoleName.CUSTOMER)).thenReturn(Optional.empty());
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");

        RegistrationException exception = assertThrows(RegistrationException.class, () -> {
            registrationService.register(requestDto);
        });

        assertEquals("Can't find role CUSTOMER", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
