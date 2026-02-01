package com.stayinn.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

	// MUST MATCH with Node.js secret
	private static final String SECRET = "my_super_secret_key_1234567890123456"; // must be at least 32 bytes for HS256
	
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET.getBytes());
	}

	public Claims extractClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	public boolean isTokenValid(String token) {
		try {
			Claims claims = extractClaims(token);
//			System.out.println("JWT VALID for user: " + claims.getSubject());
			return claims.getExpiration().after(new Date());
		} catch (JwtException | IllegalArgumentException e) {
//			System.out.println("JWT INVALID: " + e.getMessage());
			return false;
		}
	}

	public String extractEmail(String token) {
		return extractClaims(token).get("email", String.class);
	}

	public String extractRole(String token) {
		return extractClaims(token).get("role", String.class);
	}
}
