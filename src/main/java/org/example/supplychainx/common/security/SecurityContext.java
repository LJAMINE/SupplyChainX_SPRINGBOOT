package org.example.supplychainx.common.security;

public final class SecurityContext {
    private static final ThreadLocal<AuthenticatedUser> current = new ThreadLocal<>();

    public static void set(AuthenticatedUser user) {
        current.set(user);
    }

    public static AuthenticatedUser get() {
        return current.get();
    }

    public static void clear() {
        current.remove();
    }
}