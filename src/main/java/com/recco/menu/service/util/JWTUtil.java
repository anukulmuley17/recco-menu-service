//package com.recco.menu.service.util;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//
//@Component
//public class JWTUtil {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    private Key getSigningKey() {
//        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public String validateTokenAndGetTableId(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey()) // Ensure correct secret key
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//            
//            System.out.println("✅ Token validated successfully. Table ID: " + claims.getSubject());
//            return claims.getSubject(); // "sub" contains tableId
//        } catch (Exception e) {
//            System.err.println("❌ Token validation failed: " + e.getMessage());
//            throw new RuntimeException("Invalid or expired token", e);
//        }
//    }
//
//}
