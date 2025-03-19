package org.bob.siungongsi.config;

import org.bob.siungongsi.security.KakaoAuthFilter;
import org.bob.siungongsi.service.KakaoAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final KakaoAuthService kakaoAuthService;

  // 생성자 주입을 통해 KakaoAuthService 주입
  public SecurityConfig(KakaoAuthService kakaoAuthService) {
    this.kakaoAuthService = kakaoAuthService;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    KakaoAuthFilter kakaoAuthFilter =
        new KakaoAuthFilter(kakaoAuthService); // 의존성 주입된 kakaoAuthService 사용

    http.authorizeHttpRequests(
        (authorizeRequests) -> {
          authorizeRequests.anyRequest().permitAll();
        });

    http.cors(AbstractHttpConfigurer::disable);
    http.csrf(AbstractHttpConfigurer::disable);

    http.addFilterBefore(
        kakaoAuthFilter,
        UsernamePasswordAuthenticationFilter
            .class); // UsernamePasswordAuthenticationFilter 앞에 카카오 인증 필터를 추가
    return http.build();
  }
}
