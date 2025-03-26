package org.bob.siungongsi.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Base64;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JwtProviderTest {

  private JwtProvider jwtProvider;
  private String testSecretKey;
  private long testExpirationTime;

  @BeforeEach
  void setUp() {
    testSecretKey =
        Base64.getEncoder().encodeToString("test-secret-key-for-jwt-provider".getBytes());
    testExpirationTime = 3600000L; // 1시간

    jwtProvider = new JwtProvider(testSecretKey, testExpirationTime, true);
  }

  @Test
  @DisplayName("주어진 userId에 대해 유효한 JWT 토큰이 생성되어야 한다")
  void givenValidUserId_whenCreateJwtToken_thenTokenShouldBeValid() {
    String userId = "12345";
    String token = jwtProvider.createJwtToken(userId);

    assertThat(token).isNotNull().isNotEmpty();
  }

  @Test
  @DisplayName("유효한 JWT 토큰을 검증하면 올바른 사용자 ID가 반환되어야 한다")
  void givenValidJwtToken_whenValidateJwtToken_thenShouldReturnCorrectUserId() {
    String userId = "12345";
    String token = jwtProvider.createJwtToken(userId);
    Long parsedUserId = jwtProvider.validateJwtToken(token);

    assertThat(parsedUserId).isEqualTo(Long.parseLong(userId));
  }

  @Test
  @DisplayName("유효하지 않은 JWT 토큰을 검증 시 에러가 발생해야 한다")
  void givenInvalidJwtToken_whenValidateJwtToken_thenShouldThrowException() {
    String invalidToken = "invalidToken";

    CustomException exception =
        assertThrows(CustomException.class, () -> jwtProvider.validateJwtToken(invalidToken));

    assertThat(exception.getMessage()).isEqualTo("잘못된 토큰입니다");
    assertThat(exception.getErrorCode()).isEqualTo(ApiResponseCode.AUTH_ACCESS_TOKEN_EXPIRED);
  }

  @Test
  @DisplayName("만료된 JWT 토큰 검증 시 예외가 발생해야 한다.")
  void givenValidateExpiredToken_whenValidateJwtToken_thenShouldThrowException() throws Exception {
    String userId = "12345";

    jwtProvider =
        new JwtProvider(
            Base64.getEncoder().encodeToString("test-secret-key-for-jwt-provider".getBytes()),
            10,
            true);
    String token = jwtProvider.createJwtToken(userId);
    Thread.sleep(20);

    CustomException exception =
        assertThrows(CustomException.class, () -> jwtProvider.validateJwtToken(token));
    assertThat(exception.getErrorCode()).isEqualTo(ApiResponseCode.AUTH_TOKEN_EXPIRED);
    assertThat(exception.getMessage()).isEqualTo("만료된 토큰입니다");
  }

  @Test
  @DisplayName("빈 JWT 토큰을 검증 시 예외가 발생해야 한다")
  void givenEmptyJwtToken_whenValidateJwtToken_thenShouldThrowException() {
    String emptyToken = "";

    CustomException exception =
        assertThrows(CustomException.class, () -> jwtProvider.validateJwtToken(emptyToken));

    assertThat(exception.getMessage()).isEqualTo("토큰이 없습니다");
    assertThat(exception.getErrorCode()).isEqualTo(ApiResponseCode.AUTH_TOKEN_MISSING);
  }
}
