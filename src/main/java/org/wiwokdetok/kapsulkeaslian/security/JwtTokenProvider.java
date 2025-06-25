package org.wiwokdetok.kapsulkeaslian.security;

public interface JwtTokenProvider {

    String generateToken(String id, String role);

    String extractId(String token);

    String extractRole(String token);

    boolean validateToken(String token);
}
