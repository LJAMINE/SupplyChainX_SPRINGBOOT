package org.example.supplychainx.administration.mapper;

import org.example.supplychainx.administration.dto.UserRequestDto;
import org.example.supplychainx.administration.dto.UserResponseDto;
import org.example.supplychainx.administration.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDto dto);
    UserResponseDto toDto(User entity);
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User entity);
}