package com.stockiq.inventory.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private static final String SECRET =
      "stockiq_inventory_management_system_secret_key_2026_secure_very_long_key";

  private static final long EXPIRATION = 1000L * 60 * 60 * 24; // 24 hours

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generate a signed JWT token containing email, role, and userId claims.
   *
   * @param email  String
   * @param role   String
   * @param userId long
   * @return String JWT token
   */
  public String generateToken(String email, String role, long userId) {
    return Jwts.builder()
        .subject(email)
        .claim("role", role)
        .claim("userId", userId)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Extract email (subject) from JWT token.
   *
   * @param token String
   * @return String email
   */
  public String extractEmail(String token) {
    return parseClaims(token).getSubject();
  }

  /**
   * Extract userId claim from JWT token.
   *
   * @param token String
   * @return Long userId
   */
  public Long extractUserId(String token) {
    Object userId = parseClaims(token).get("userId");
    if (userId instanceof Number) {
      return ((Number) userId).longValue();
    }
    return null;
  }

  /**
   * Extract role claim from JWT token.
   *
   * @param token String
   * @return String role
   */
  public String extractRole(String token) {
    return parseClaims(token).get("role", String.class);
  }

  /**
   * Validate token — parses and checks expiration.
   *
   * @param token String
   * @return boolean true if valid
   */
  public boolean validateToken(String token) {
    try {
      Claims claims = parseClaims(token);
      return claims.getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}