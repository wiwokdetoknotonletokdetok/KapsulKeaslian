package org.wiwokdetok.kapsulkeaslian.security;

import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface JwtTokenProvider {

    @Deprecated
    String generateToken(String id, String role);

    String generateToken(UUID id, String role);

    @Deprecated
    String extractId(String token);

    @Deprecated
    String extractRole(String token);

    @Deprecated
    boolean validateToken(String token);

    Claims decodeToken(String token);

    String getId(Claims payload);

    String getRole(Claims payload);
}
