package org.gaung.wiwokdetok.kapsulkeaslian.security;

import io.jsonwebtoken.Claims;

import java.util.UUID;

public interface JwtTokenProvider {

    String generateToken(UUID id, String role);

    Claims decodeToken(String token);

    String getId(Claims payload);

    String getRole(Claims payload);
}
