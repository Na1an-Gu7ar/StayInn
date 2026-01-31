package com.stayinn.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	// 🔴 MUST MATCH Node.js secret
//	private static final String SECRET = "my_super_secret_key_123456";
	private static final String SECRET = "mySuperSecretKey";
	
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET.getBytes());
	}

	public Claims extractClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}

	public boolean isTokenValid(String token) {
		try {
			Claims claims = extractClaims(token);
			return claims.getExpiration().after(new Date());
		} catch (JwtException | IllegalArgumentException e) {
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
