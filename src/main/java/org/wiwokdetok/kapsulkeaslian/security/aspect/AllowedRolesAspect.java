package org.wiwokdetok.kapsulkeaslian.security.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;
import org.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class AllowedRolesAspect {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Before("@annotation(org.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles)")
    public void checkAllowedRoles(JoinPoint joinPoint) {
        String token = getToken();

        if (!jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        String roleFromToken = jwtTokenProvider.extractRole(token);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AllowedRoles allowedRoles = method.getAnnotation(AllowedRoles.class);
        List<String> allowed = Arrays.asList(allowedRoles.value());

        if (!allowed.contains(roleFromToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    private static String getToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        HttpServletRequest request = attrs.getRequest();
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid token");
        }

        token = token.substring(7);
        return token;
    }
}
