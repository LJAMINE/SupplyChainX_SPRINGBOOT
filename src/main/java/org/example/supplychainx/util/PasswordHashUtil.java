package org.example.supplychainx.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * One-time utility to generate BCrypt hash for a password.
 * Run: mvn exec:java -Dexec.mainClass="org.example.supplychainx.util.PasswordHashUtil" -Dexec.args="secret"
 */
public class PasswordHashUtil {
    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "secret";
        String hash = new BCryptPasswordEncoder().encode(password);
        System.out.println("BCrypt hash for \"" + password + "\":");
        System.out.println(hash);
        System.out.println("\nSQL: UPDATE users SET password_hash = '" + hash + "' WHERE email = 'GESTIONNAIRE_APPROVISIONNEMENT@gmail.com';");
    }
}
