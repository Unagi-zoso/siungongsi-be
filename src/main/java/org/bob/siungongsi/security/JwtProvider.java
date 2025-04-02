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
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

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
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
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
      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_EXPIRED);
    } catch (IllegalArgumentException e) {
      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_MISSING);
    } catch (SignatureException e) {
      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_INVALID_SIGNATURE);
    } catch (MalformedJwtException e) {
      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_MALFORMED);
    } catch (UnsupportedJwtException e) {
      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_UNSUPPORTED);
    } catch (Exception e) {
      throw new CustomException(ApiResponseCode.AUTH_INTERNAL_SERVER_ERROR);
    }
  }
}
