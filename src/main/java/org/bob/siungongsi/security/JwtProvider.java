package org.bob.siungongsi.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.expiration-time}")
  private long expirationTime;

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)); // 최신 방식
  }

  public String createJwtToken(String userId) {
    return Jwts.builder()
        .subject(userId)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getKey())
        .compact();
  }

  public Long validateJwtToken(String token) {
    try {
      return Long.parseLong(
          Jwts.parser()
              .verifyWith(getKey())
              .build()
              .parseSignedClaims(token)
              .getPayload()
              .getSubject());
    } catch (Exception e) {
      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_EXPIRED, "잘못된 토큰입니다");
    }
  }
}
