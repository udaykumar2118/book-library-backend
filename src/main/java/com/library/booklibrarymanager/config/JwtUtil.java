package com.library.booklibrarymanager.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long expirationTime;
    
    public String generateToken(String email) {
        System.out.println("=== JWT DEBUG ===");
        System.out.println("Generating token for: " + email);
        System.out.println("Secret key length: " + (secretKey != null ? secretKey.length() : 0));
        
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationTime);
            
            // Ensure we have a strong key
            String strongKey = secretKey;
            if (strongKey == null || strongKey.length() < 32) {
                System.out.println("WARNING: Weak key, using strong default");
                strongKey = "ThisIsAVeryStrongSecretKeyForJWT256BitSecurity1234567890!";
            }
            
            System.out.println("Using key length: " + strongKey.length());
            
            String token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS256, strongKey)
                    .compact();
            
            System.out.println("Token generated successfully!");
            return token;
            
        } catch (Exception e) {
            System.out.println("JWT Error: " + e.getMessage());
            // Return simple token for testing
            return "simple-token-" + email + "-" + System.currentTimeMillis();
        }
    }
    
    public String extractEmail(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            System.out.println("Token extraction error: " + e.getMessage());
            return null;
        }
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("Token validation error: " + e.getMessage());
            return false;
        }
    }
}