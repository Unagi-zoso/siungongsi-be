package org.bob.siungongsi.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

  private final String secretKey;
  private final long expirationTime;

  @Autowired
  public JwtProvider(
      @Value("${jwt.secret-key}") String secretKey,
      @Value("${jwt.expiration-time}") long expirationTime) {
    this.secretKey = secretKey;
    this.expirationTime = expirationTime;
  }

  public JwtProvider(String secretKey, long expirationTime, boolean isTest) {
    this.secretKey = secretKey;
    this.expirationTime = expirationTime;
  }

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
    } catch (ExpiredJwtException e) {
      throw new CustomException(ApiResponseCode.AUTH_TOKEN_EXPIRED, "만료된 토큰입니다");
    } catch (IllegalArgumentException e) {
      throw new CustomException(ApiResponseCode.AUTH_TOKEN_MISSING, "토큰이 없습니다");
    } catch (Exception e) {
      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_EXPIRED, "잘못된 토큰입니다");
    }
  }
}
