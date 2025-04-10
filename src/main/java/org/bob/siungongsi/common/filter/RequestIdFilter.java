package org.bob.siungongsi.common.filter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@Component
public class RequestIdFilter implements Filter {

  private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    LocalDate now = LocalDate.now(KOREA_ZONE);
    String requestId = UUID.randomUUID().toString().substring(0, 8);
    MDC.put("requestId", requestId + "-" + now.toString().replace(":", "-").replace(".", "-"));
    try {
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
