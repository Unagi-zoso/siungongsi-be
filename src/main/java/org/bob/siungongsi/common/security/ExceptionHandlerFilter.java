package org.bob.siungongsi.common.security;

import java.io.IOException;

import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      handleException(response, e);
    }
  }

  private void handleException(HttpServletResponse response, Exception e) throws IOException {
    int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    String message = "서버 내부 오류가 발생했습니다.";
    int code = ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION.getCode();

    if (e instanceof CustomException customException) {
      status = HttpServletResponse.SC_UNAUTHORIZED;
      message = e.getMessage();
      code = customException.getErrorCode().getCode();
    }

    response.setStatus(status);
    response.setContentType("application/json;charset=UTF-8");
    String jsonResponse = String.format("{\"code\": %d, \"message\": \"%s\"}", code, message);
    response.getWriter().write(jsonResponse);
    response.getWriter().flush();
  }
}
