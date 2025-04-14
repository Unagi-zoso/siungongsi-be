package org.bob.siungongsi.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Aspect
@Component
public class ApiPerformanceLoggerAspect {

  private static final Logger logger = LoggerFactory.getLogger(ApiPerformanceLoggerAspect.class);

  @Around(
      "execution(public * org.bob.siungongsi.api.controller..*(..)) || "
          + "execution(public * org.bob.siungongsi.api.service..*(..))")
  public Object logApiTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();

    Object result = joinPoint.proceed();

    long elapsed = System.currentTimeMillis() - start;

    ServletRequestAttributes attr =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
    String methodName = joinPoint.getSignature().getName();

    if (attr != null) {
      HttpServletRequest request = attr.getRequest();
      HttpServletResponse response = attr.getResponse();

      String method = request.getMethod();
      String uri = request.getRequestURI();
      int status = (response != null) ? response.getStatus() : -1;

      String requestId = MDC.get("requestId");

      logger.info(
          "[PERF][{}] {} {} status={} time={}ms [{}#{}]",
          requestId,
          method,
          uri,
          status,
          elapsed,
          className,
          methodName);
    } else {
      logger.info("[PERF] {}#{} executed in {}ms", className, methodName, elapsed);
    }

    return result;
  }
}
