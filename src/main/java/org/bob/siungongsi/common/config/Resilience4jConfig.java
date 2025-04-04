package org.bob.siungongsi.common.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class Resilience4jConfig {

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    CircuitBreakerConfig config =
        CircuitBreakerConfig.custom()
            .failureRateThreshold(50) // 실패율 50% 이상이면 차단
            .permittedNumberOfCallsInHalfOpenState(1) // Half-Open 상태에서 1번 테스트
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // 카운트 기반 윈도우
            .slidingWindowSize(2) // 2개 요청 단위로 실패율 계산
            .minimumNumberOfCalls(2) // 최소 2번 호출해야 실패율 계산
            .waitDurationInOpenState(Duration.ofMillis(500)) // 500ms 동안 차단 유지
            .automaticTransitionFromOpenToHalfOpenEnabled(true) // 자동으로 Half-Open 전환
            .build();

    return CircuitBreakerRegistry.of(config);
  }

  @Bean
  public CircuitBreaker stockPriceCircuitBreaker(CircuitBreakerRegistry registry) {
    return registry.circuitBreaker("stockPriceService");
  }
}
