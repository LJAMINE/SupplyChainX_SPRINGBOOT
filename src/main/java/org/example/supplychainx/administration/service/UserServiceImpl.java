package org.example.supplychainx.administration.service;

import org.example.supplychainx.administration.dto.UserRequestDto;
import org.example.supplychainx.administration.dto.UserResponseDto;
import org.example.supplychainx.administration.entity.User;
import org.example.supplychainx.administration.mapper.UserMapper;
import org.example.supplychainx.administration.repository.UserRepository;
import org.example.supplychainx.common.exception.BadRequestException;
import org.example.supplychainx.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<UserResponseDto> list(String search, Pageable pageable) {
        Page<User> page = (search == null || search.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, search, pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public UserResponseDto get(Long id) {
        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

//    @Override
//    public UserResponseDto create(UserRequestDto dto) {
//        if (userRepository.existsByEmail(dto.getEmail())) {
//            throw new BadRequestException("Email already in use");
//        }
//        User user = mapper.toEntity(dto);
//        user.setCreatedAt(LocalDateTime.now());
//        // DEV NOTE: plaintext password stored in passwordHash field for dev. Replace with BCrypt in production.
//        user.setPasswordHash(dto.getPassword());
//        User saved = userRepository.save(user);
//        return mapper.toDto(saved);
//    }


    @Override
    public UserResponseDto create(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        User user = mapper.toEntity(dto);
        System.out.println("DEBUG Service: mapped entity BEFORE set/save = " + user);
        user.setCreatedAt(LocalDateTime.now());
        // DEV: store password as-is for now (replace with BCrypt later)
        if (dto.getPassword() != null) {
            user.setPasswordHash(dto.getPassword());
        }
        User saved = userRepository.save(user);
        return mapper.toDto(saved);
    }
    @Override
    public UserResponseDto update(Long id, UserRequestDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        mapper.updateEntityFromDto(dto, user);
        user.setUpdatedAt(LocalDateTime.now());
        // If password provided, update it (still plaintext in dev)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPasswordHash(dto.getPassword());
        }
        User saved = userRepository.save(user);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}