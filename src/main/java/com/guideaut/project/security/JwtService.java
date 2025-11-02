package com.guideaut.project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

  private final SecretKey key;
  private final long accessMinutes;

  public JwtService(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.access-minutes:30}") long accessMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); // >= 32 bytes
    this.accessMinutes = accessMinutes;
  }

  public String generateAccess(String subject, Map<String, ?> claims) {
    Instant now = Instant.now();

    JwtBuilder builder = Jwts.builder().setSubject(subject);
    if (claims != null) {
      claims.forEach((k, v) -> builder.claim(k, v)); // adiciona claims uma a uma
    }

    return builder
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plus(Duration.ofMinutes(accessMinutes))))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /** Valida e retorna o JWS (lança exceção se inválido/expirado). */
  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token);
  }
}
