package com.library.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final long accessTokenTtlMs;
    private final long refreshTokenTtlMs;

    public JwtTokenService(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.access-token-ttl-ms:900000}") long accessTokenTtlMs,
            @Value("${app.jwt.refresh-token-ttl-ms:604800000}") long refreshTokenTtlMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessTokenTtlMs = accessTokenTtlMs;
        this.refreshTokenTtlMs = refreshTokenTtlMs;
    }

    public String generateAccessToken(UserDetails user, Map<String, Object> extraClaims) {
        return buildToken(user.getUsername(), extraClaims, accessTokenTtlMs);
    }

    public String generateRefreshToken(UserDetails user) {
        return buildToken(user.getUsername(), Map.of("type", "refresh"), refreshTokenTtlMs);
    }

    public boolean isTokenValid(String token, UserDetails user) {
        String username = getUsername(token);
        return username.equals(user.getUsername()) && !isExpired(token);
    }

    public boolean isRefreshToken(String token) {
        try {
            Object type = getAllClaims(token).get("type");
            return type != null && "refresh".equals(type.toString());
        } catch (Exception ex) {
            return false;
        }
    }

    public String generateAccessFromRefresh(String refreshToken, UserDetails user) {
        if (!isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        return generateAccessToken(user, Map.of());
    }

    public String getUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean isExpired(String token) {
        return getAllClaims(token).getExpiration().before(new Date());
    }

    private String buildToken(String subject, Map<String, Object> extraClaims, long ttlMs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ttlMs)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
