package com.example.demogc.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements Serializable {

    @Value("${jwt.token.validity}")
    private long tokenValidity;

    @Value("${jwt.signing.key}")
    private String signingKey;

    @Value("${jwt.authorities.key}")
    private String authoritiesKey;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        byte[] keyBytes = signingKey.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT signing key must be at least 32 bytes long");
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(authoritiesKey, authorities)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public UsernamePasswordAuthenticationToken getAuthenticationToken(String token, UserDetails userDetails) {
        final Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);

        final Claims claims = claimsJws.getBody();
        final Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(authoritiesKey)
                        .toString()
                        .split(","))
                .filter(authority -> !authority.isBlank())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }
}
