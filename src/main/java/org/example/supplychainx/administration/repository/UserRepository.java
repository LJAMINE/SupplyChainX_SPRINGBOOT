package org.example.supplychainx.administration.repository;

import org.example.supplychainx.administration.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fn, String ln, String email, Pageable pageable);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email); // required by AuthService
}