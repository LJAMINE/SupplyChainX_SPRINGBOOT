package org.example.supplychainx.config;


import org.example.supplychainx.administration.entity.User;
import org.example.supplychainx.administration.repository.UserRepository;
import org.example.supplychainx.common.security.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;


//UserDetailsService implementation (load user from DB)

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Check log
        System.out.println("User found: " + user.getEmail() + ", Role: " + user.getRole());

        // Map user domain object to UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash()) // MUST be hashed
                .authorities("ROLE_" + user.getRole().name()) // Ensure role mapping is correct
                .build();
    }
}
