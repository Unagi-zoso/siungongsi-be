package org.bob.siungongsi.security;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {

  @Mock private JwtProvider jwtProvider;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain chain;

  private JwtAuthFilter jwtAuthFilter;

  @BeforeEach
  void setUp() {
    jwtAuthFilter = new JwtAuthFilter(jwtProvider);
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("공개된 API 요청 시 필터를 통과해야 한다")
  void givenPublicUri_whenDoFilterInternal_thenShouldPassFilterWithoutAuth()
      throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/v1/auth/login");

    jwtAuthFilter.doFilterInternal(request, response, chain);

    verify(chain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("공시 상세 조회 API 요청 시 인증 토큰이 없으면 필터를 통과해야 한다")
  void givenGongsiDetailUriWithoutToken_whenDoFilterInternal_thenShouldPassFilter()
      throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/v1/gongsi/123");
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtAuthFilter.doFilterInternal(request, response, chain);

    verify(chain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  @DisplayName("공시 상세 조회 API 요청 시 인증 토큰이 있으면 사용자를 인증해야 한다")
  void givenGongsiDetailUriWithToken_whenDoFilterInternal_thenShouldAuthenticateUser()
      throws ServletException, IOException {
    String token = "valid.jwt.token";
    Long userId = 12345L;

    when(request.getRequestURI()).thenReturn("/v1/gongsi/123");
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtProvider.validateJwtToken(token)).thenReturn(userId);

    jwtAuthFilter.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .isEqualTo(userId);
    verify(chain).doFilter(request, response);
  }

  @Test
  @DisplayName("유효한 토큰이 포함된 요청은 사용자를 인증해야 한다")
  void givenValidToken_whenDoFilterInternal_thenShouldAuthenticateUser() throws Exception {
    String token = "valid.jwt.token";
    Long userId = 12345L;

    when(request.getRequestURI()).thenReturn("/v1/protected");
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtProvider.validateJwtToken(token)).thenReturn(userId);

    jwtAuthFilter.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .isEqualTo(userId);
    verify(chain).doFilter(request, response);
  }

  @Test
  @DisplayName("만료된 토큰이 포함된 요청은 예외를 발생시켜야 한다")
  void givenInvalidToken_whenDoFilterInternal_thenShouldThrowException() {
    String invalidToken = "invalid.jwt.token";

    when(request.getRequestURI()).thenReturn("/v1/protected");
    when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
    when(jwtProvider.validateJwtToken(invalidToken))
        .thenThrow(new CustomException(ApiResponseCode.AUTH_TOKEN_EXPIRED, "만료된 토큰입니다"));

    CustomException exception =
        assertThrows(
            CustomException.class, () -> jwtAuthFilter.doFilterInternal(request, response, chain));

    assertThat(exception.getErrorCode()).isEqualTo(ApiResponseCode.AUTH_TOKEN_EXPIRED);
    assertThat(exception.getMessage()).isEqualTo("만료된 토큰입니다");
  }

  @Test
  @DisplayName("토큰이 포함되지 않은 요청은 예외를 발생시켜야 한다")
  void givenNoToken_whenDoFilterInternal_thenShouldThrowException() {
    when(request.getRequestURI()).thenReturn("/v1/protected");
    when(request.getHeader("Authorization")).thenReturn(null);

    CustomException exception =
        assertThrows(
            CustomException.class, () -> jwtAuthFilter.doFilterInternal(request, response, chain));

    assertThat(exception.getErrorCode()).isEqualTo(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION);
    assertThat(exception.getMessage()).isEqualTo("엑세스 토큰을 넣어주세요");
  }
}
