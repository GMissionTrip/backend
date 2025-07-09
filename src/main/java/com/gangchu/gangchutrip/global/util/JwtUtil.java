package com.gangchu.gangchutrip.global.util;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private JwtBuilder jwtBuilder(String username, long expirationMills) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMills))
                .signWith(key);
    }

    public String generateAccessToken(String username) {
        return jwtBuilder(username, accessExpiration).compact();
    }

    public String generateRefreshToken(String username) {
        return jwtBuilder(username, refreshExpiration).compact();
    }

    private io.jsonwebtoken.JwtParser createJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    public boolean validateToken(String token) {
        try {
            createJwtParser().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException |
                 io.jsonwebtoken.UnsupportedJwtException |
                 io.jsonwebtoken.MalformedJwtException |
                 IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return createJwtParser()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
