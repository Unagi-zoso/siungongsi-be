package org.bob.siungongsi.common.security;

import java.io.IOException;

import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.util.RedisUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtProvider jwtProvider;
  private final RedisUtils redisUtils;

  public JwtAuthFilter(JwtProvider jwtProvider, RedisUtils redisUtils) {
    this.jwtProvider = jwtProvider;
    this.redisUtils = redisUtils;
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

    if (request.getRequestURI().startsWith("/v1/gongsi/")) {
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
      }
    }

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new CustomException(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION);
    }

    authHeader = authHeader.replace("Bearer ", "");

    if (redisUtils.hasKeyBlackList("blacklist:" + authHeader)) {
      throw new CustomException(ApiResponseCode.AUTH_ACCESS_TOKEN_EXPIRED);
    }

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
        || uri.equals("/")
        || uri.matches("/swagger-ui.*")
        || uri.matches("/support.*")
        || uri.matches("/v1/companies.*")
        || uri.matches("/v3/api-docs.*")
        || uri.matches("/swagger-resources.*");
  }
}
