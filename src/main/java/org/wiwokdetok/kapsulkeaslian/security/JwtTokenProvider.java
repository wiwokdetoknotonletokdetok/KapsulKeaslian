package org.wiwokdetok.kapsulkeaslian.security;

import io.jsonwebtoken.Claims;

public interface JwtTokenProvider {

    String generateToken(String id, String role);

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
