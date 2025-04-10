package org.bob.siungongsi.batch;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("batch")
public class ThirdPartyApiLoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(ThirdPartyApiLoggingAspect.class);

  @Around("execution(* org.bob.siungongsi.batch.client.clientinterface.*Wrapper.*(..))")
  public Object logAroundThirdPartyApi(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();
    String argsString = summarizeArgs(args);

    long start = System.currentTimeMillis();
    try {
      logger.info("[{} Call Started] method={}, args={}", className, methodName, argsString);
      Object result = joinPoint.proceed();
      long duration = System.currentTimeMillis() - start;
      logger.info("[{} Call Completed] method={}, duration={}ms", className, methodName, duration);
      return result;
    } catch (Exception ex) {
      long duration = System.currentTimeMillis() - start;
      logger.warn(
          "[{} Call Failed] method={}, duration={}ms, exception={}",
          className,
          methodName,
          duration,
          ex.getMessage(),
          ex);
      throw ex;
    }
  }

  private String summarizeArgs(Object[] args) {
    if (args == null || args.length == 0) {
      return "[]";
    }

    return Arrays.stream(args)
        .map(
            arg -> {
              if (arg == null) return "null";
              String str = arg.toString();
              return str.length() > 300 ? str.substring(0, 300) + "...)" : str;
            })
        .collect(Collectors.joining(", ", "[", "]"));
  }
}
