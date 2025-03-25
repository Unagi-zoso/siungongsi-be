package org.bob.siungongsi.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

  private static final String SECRET_KEY = "dflknlsdfjlskfncdsjlkmxnfjsklcxmvnfddddddddc";
  private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)); // 최신 방식
  }

  public String createJwtToken(String userId) {
    return Jwts.builder()
        .subject(userId)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(getKey())
        .compact();
  }

  public Long validateJwtToken(String token) {
    //    try {
    return Long.parseLong(
        Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject());
    //    } catch (Exception e) {
    //      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_EXPIRED, "잘못된 토큰입니다");
    //    }
  }
}
