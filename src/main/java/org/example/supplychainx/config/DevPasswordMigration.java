//package org.example.supplychainx.config;
//
//import org.example.supplychainx.administration.entity.User;
//import org.example.supplychainx.administration.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * DEV-only: re-hash plaintext passwords in users.password_hash to BCrypt.
// * Use a non-production profile (e.g. "migrate") and run once.
// */
//@Component
//@Profile("migrate-passwords")
//public class DevPasswordMigration implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public DevPasswordMigration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        List<User> users = userRepository.findAll();
//        for (User u : users) {
//            String stored = u.getPasswordHash();
//            if (stored == null) continue;
//            // Detect common bcrypt prefixes
//            boolean isBcrypt = stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$");
//            if (!isBcrypt) {
//                String plain = stored; // your DB currently contains plaintext
//                String hashed = passwordEncoder.encode(plain);
//                u.setPasswordHash(hashed);
//                userRepository.save(u);
//                System.out.println("Re-hashed user " + u.getEmail());
//            } else {
//                System.out.println("Skipping already-hashed user " + u.getEmail());
//            }
//        }
//        System.out.println("Password migration completed. Remove the migration runner afterward.");
//    }
//}