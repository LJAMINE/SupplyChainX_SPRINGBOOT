package org.example.supplychainx.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class SecurityAspect {

    private final AuthService authService;

    public SecurityAspect(AuthService authService) {
        this.authService = authService;
    }

    // Only intercept methods or classes annotated with @RequireRole
    @Around("@within(org.example.supplychainx.common.security.RequireRole) || @annotation(org.example.supplychainx.common.security.RequireRole)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new RuntimeException("No request context");
        }
        HttpServletRequest req = attrs.getRequest();

        String email = req.getHeader("X-User-Email");
        String password = req.getHeader("X-User-Password");

        // authenticate (throws UnauthorizedException on failure)

        AuthenticatedUser principal = authService.authenticate(email, password);

        // decide required roles (method-level overrides class-level)
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method method = ms.getMethod();
        RequireRole rr = AnnotationUtils.findAnnotation(method, RequireRole.class);
        if (rr == null) {
            rr = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequireRole.class);
        }

        if (rr != null && rr.value().length > 0) {
            boolean allowed = Arrays.stream(rr.value()).anyMatch(r -> r == principal.getRole());
            if (!allowed) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access denied");
            }
        }

        try {
            SecurityContext.set(principal);
            return pjp.proceed();
        } finally {
            SecurityContext.clear();
        }
    }
}