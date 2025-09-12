package com.gangchu.gangchutrip.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private Key key;

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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

    public boolean validate(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public Claims claims(String token){
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody();
    }

    public Authentication getAuthentication(String token){
        Claims claims = claims(token);
        String username = claims.getSubject(); // sub = memberId
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");

        var authorities = (roles == null) ? List.<GrantedAuthority>of()
            : roles.stream().map(SimpleGrantedAuthority::new).toList();

        MemberPrincipal principal = new MemberPrincipal(username, authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
