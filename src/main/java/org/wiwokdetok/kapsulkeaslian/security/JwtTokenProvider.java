package org.wiwokdetok.kapsulkeaslian.security;

public interface JwtTokenProvider {

    String generateToken(String id, String email, String role);

    String extractId(String token);

    String extractEmail(String token);

    String extractRole(String token);

    boolean validateToken(String token);
}
