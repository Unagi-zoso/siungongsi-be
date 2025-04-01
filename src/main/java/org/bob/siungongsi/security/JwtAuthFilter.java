package org.bob.siungongsi.security;

import java.io.IOException;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
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

    if (isPublicUri(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader("Authorization");

    if (request.getRequestURI().matches("/v1/gongsi/\\d+")) {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }
    }

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new CustomException(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION, "엑세스 토큰을 넣어주세요");
    }

    authHeader = authHeader.replace("Bearer ", "");
    Long userId = jwtProvider.validateJwtToken(authHeader);

    JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(userId);
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    filterChain.doFilter(request, response);
  }

  private boolean isPublicUri(String uri) {
    return uri.equals("/v1/auth/login")
        || uri.equals("/v1/auth/register")
        || uri.equals("/v1/auth/terms")
        || uri.equals("/v1/gongsi")
        || uri.equals("/health")
        || uri.matches("/swagger-ui.*")
        || uri.matches("/admin.*")
        || uri.matches("/v1/companies.*")
        || uri.matches("/v3/api-docs.*")
        || uri.matches("/swagger-resources.*");
  }
}
