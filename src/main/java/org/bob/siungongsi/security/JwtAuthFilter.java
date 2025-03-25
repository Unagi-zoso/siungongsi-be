package org.bob.siungongsi.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtProvider jwtProvider;

  public JwtAuthFilter(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getRequestURI().equals("/v1/auth/login")
        || request.getRequestURI().equals("/v1/auth/register")) {
      filterChain.doFilter(request, response); // 필터링을 건너뛰고 요청을 그대로 처리
      return;
    }

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    authHeader = authHeader.replace("Bearer ", "");
    Long userId = jwtProvider.validateJwtToken(authHeader);

    if (userId != null) {
      JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(userId);
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    filterChain.doFilter(request, response);
  }
}
