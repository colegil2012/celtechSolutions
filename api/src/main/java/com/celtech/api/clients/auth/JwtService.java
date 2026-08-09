package com.celtech.api.clients.auth;

import com.celtech.api.clients.model.ClientUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey key;
    private final long ttlMinutes;

    public JwtService(
            @Value("${app.auth.jwt-secret}") String secret,
            @Value("${app.auth.jwt-ttl-minutes}") long ttlMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlMinutes = ttlMinutes;
    }

    public String issue(ClientUser user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("siteIds", user.getSiteIds())
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(now.plus(ttlMinutes, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public AuthPrincipal parse(String token) {
        Claims c = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new AuthPrincipal(
                c.getSubject(),
                c.get("email", String.class),
                c.get("role", String.class),
                (List<String>) c.get("siteIds", List.class));
    }

    public long ttlSeconds() {
        return ttlMinutes * 60;
    }
}