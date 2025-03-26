package org.bob.siungongsi.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerFilterTest {

  private ExceptionHandlerFilter exceptionHandlerFilter;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    exceptionHandlerFilter = new ExceptionHandlerFilter();
  }

  @Test
  @DisplayName("예외가 없을 경우 필터가 정상적으로 진행되어야 한다.")
  void givenNoException_whenDoFilterInternal_thenShouldProceedNormally()
      throws ServletException, IOException {
    doNothing().when(filterChain).doFilter(request, response);

    exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(anyInt());
  }

  @Test
  @DisplayName("CustomException 발생 시 예외가 올바르게 처리되어야 한다.")
  void givenCustomException_whenDoFilterInternal_thenShouldHandleCustomException()
      throws ServletException, IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    CustomException customException =
        new CustomException(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION, "Authentication failed");

    doThrow(customException).when(filterChain).doFilter(request, response);
    when(response.getWriter()).thenReturn(printWriter);

    exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json;charset=UTF-8");

    String expectedJson =
        String.format(
            "{\"code\": %d, \"message\": \"%s\"}",
            ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION.getCode(), "Authentication failed");

    assertEquals(expectedJson, stringWriter.toString().trim());
  }

  @Test
  @DisplayName("GenericException 발생 시 서버 내부 오류로 처리되어야 한다.")
  void givenGenericException_whenDoFilterInternal_thenShouldHandleAsInternalServerError()
      throws ServletException, IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    Exception genericException = new RuntimeException("Something went wrong");

    doThrow(genericException).when(filterChain).doFilter(request, response);
    when(response.getWriter()).thenReturn(printWriter);

    exceptionHandlerFilter.doFilterInternal(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    verify(response).setContentType("application/json;charset=UTF-8");

    String expectedJson =
        String.format(
            "{\"code\": %d, \"message\": \"%s\"}",
            ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION.getCode(), "서버 내부 오류가 발생했습니다.");

    assertEquals(expectedJson, stringWriter.toString().trim());
  }
}
