package org.gaung.wiwokdetok.kapsulkeaslian.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderImplTest {

    private JwtTokenProviderImpl jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProviderImpl();

        ReflectionTestUtils.setField(jwtTokenProvider, "secret", "supersecretkeysupersecretkey123456");
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", 3600000L);
        jwtTokenProvider.init();
    }

    @Test
    void testGenerateAndDecodeToken() {
        UUID userId = UUID.randomUUID();
        String role = "USER";

        String token = jwtTokenProvider.generateToken(userId, role);
        assertNotNull(token);

        Claims claims = jwtTokenProvider.decodeToken(token);

        assertEquals(userId.toString(), jwtTokenProvider.getId(claims));
        assertEquals(role, jwtTokenProvider.getRole(claims));
    }

    @Test
    void testInvalidTokenThrowsException() {
        String invalidToken = "this.is.an.invalid.token";

        assertThrows(JwtException.class, () -> {
            jwtTokenProvider.decodeToken(invalidToken);
        });
    }
}
