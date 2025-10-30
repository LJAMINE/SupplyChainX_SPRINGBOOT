package org.example.supplychainx.administration.service;

import org.example.supplychainx.administration.dto.UserRequestDto;
import org.example.supplychainx.administration.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {


    Page<UserResponseDto> list(String search, Pageable pageable);
    UserResponseDto get(Long id);
    UserResponseDto create(UserRequestDto dto);
    UserResponseDto update(Long id, UserRequestDto dto);
    void delete(Long id);
}