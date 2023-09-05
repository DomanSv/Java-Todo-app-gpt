package com.todo.todoApp.service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@UtilityClass
public class JwtUtils {

  private static final String SECRET_KEY = "Z9KqemufnuvDvEpOHpA/PbJD4pM7LPmzox6QScapBw6rOCT9WwnoItXPdjxP+uwjwV3fg5RuUtiqEngm3cvJ7A==";// Make sure to keep this secret

  public static String generateJwtToken(String username) {
    //Builds the JWT and serializes it to a compact, URL-safe string
    return Jwts
        .builder()
        .setSubject(username)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public static String extractUsernameFromToken(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private static Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private static Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

}
