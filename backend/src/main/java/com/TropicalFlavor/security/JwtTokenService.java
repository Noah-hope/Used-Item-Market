package com.TropicalFlavor.security;

import com.TropicalFlavor.model.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenService {
    private static final long EXPIRE_MILLIS = 7L * 24L * 60L * 60L * 1000L;
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "used-item-market-jwt-secret-key-for-spring-mvc-demo-2026".getBytes(StandardCharsets.UTF_8)
    );

    public String generateToken(String uid, boolean admin) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(uid)
                .claim("admin", admin)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRE_MILLIS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public AuthUser parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return new AuthUser(claims.getSubject(), Boolean.TRUE.equals(claims.get("admin", Boolean.class)));
    }
}
