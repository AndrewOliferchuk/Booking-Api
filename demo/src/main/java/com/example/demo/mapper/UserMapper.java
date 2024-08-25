package com.example.demo.mapper;

import com.example.demo.config.MapperConfig;
import com.example.demo.dto.registration.UserRegistrationRequestDto;
import com.example.demo.dto.user.UserResponseDto;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToRoleNames")
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    default void setRoles(@MappingTarget UserResponseDto userResponseDto, User user) {
        Set<Role.RoleName> roleNames = user.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
        userResponseDto.roles().addAll(roleNames);
    }

    @Named("rolesToRoleNames")
    default Set<Role.RoleName> rolesToRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());
    }
}
