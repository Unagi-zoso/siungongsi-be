package org.bob.siungongsi.security;

import java.io.IOException;

import org.bob.siungongsi.service.KakaoAuthService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class KakaoAuthFilter extends OncePerRequestFilter {
  private final KakaoAuthService kakaoAuthService;

  public KakaoAuthFilter(KakaoAuthService kakaoAuthService) {
    this.kakaoAuthService = kakaoAuthService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response); // 토큰이 없으면 그냥 필터 체인 진행
      return;
    }

    String accessToken = authHeader.substring(7); // "Bearer "를 제거한 토큰

    // 토큰 검증
    String socialId = kakaoAuthService.getSocialIdFromAccessToken(accessToken);
    if (socialId != null) {
      // 토큰이 유효하면 인증 처리 (SecurityContext에 인증 객체 저장)
      KakaoAuthenticationToken authenticationToken = new KakaoAuthenticationToken(socialId);
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    } else {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 유효하지 않은 토큰
      response.getWriter().write("Invalid Access Token");
      return;
    }

    filterChain.doFilter(request, response); // 필터 체인 계속 진행
  }
}
