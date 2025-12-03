package com.guideaut.project.security;

import com.guideaut.project.identity.Usuario; // Importante
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-minutes:30}") long accessMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMinutes = accessMinutes;
    }

    // --- NOVO MÉTODO ESPECÍFICO PARA USUÁRIO ---
    public String generateToken(Usuario usuario) {
        // Extrai as roles do banco para uma lista de Strings
        List<String> roles = usuario.getPapeis().stream()
                .map(papel -> papel.getNome()) // Pega "ADMIN"
                .collect(Collectors.toList());

        // Coloca no Map de Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles); 
        claims.put("nome", usuario.getNome()); // Opcional: colocar nome no token
        claims.put("id", usuario.getId().toString());

        return generateAccess(usuario.getEmail(), claims);
    }
    // -------------------------------------------

    public String generateAccess(String subject, Map<String, ?> claims) {
        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder().setSubject(subject);
        if (claims != null) {
            claims.forEach((k, v) -> builder.claim(k, v));
        }

        return builder
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(Duration.ofMinutes(accessMinutes))))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}