package org.bob.siungongsi.api.config;

import org.bob.siungongsi.common.security.ExceptionHandlerFilter;
import org.bob.siungongsi.common.security.JwtAuthFilter;
import org.bob.siungongsi.common.security.JwtProvider;
import org.bob.siungongsi.common.util.RedisUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// 이후 시스템 안정화가 이뤄지면 개발, 운영용으로 분리 필요. origin 허용 범위 대상
@Profile("!batch")
@Configuration
public class SecurityConfigForApi {

  private final JwtProvider jwtProvider;
  private final RedisUtils redisUtils;

  private final CorsProperties corsProperties;

  public SecurityConfigForApi(
      JwtProvider jwtProvider, RedisUtils redisUtils, CorsProperties corsProperties) {
    this.jwtProvider = jwtProvider;
    this.redisUtils = redisUtils;
    this.corsProperties = corsProperties;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(
        (authorizeRequests) -> {
          authorizeRequests.anyRequest().permitAll();
        });

    http.cors(Customizer.withDefaults());
    http.csrf(AbstractHttpConfigurer::disable);

    http.addFilterBefore(
        new JwtAuthFilter(jwtProvider, redisUtils), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(new ExceptionHandlerFilter(), JwtAuthFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    // configuration.setAllowedOrigins(corsProperties.allowedOrigins());
    configuration.addAllowedOrigin(
        "*"); // front 가 로컬에서도 접근하고 싶다 요구해서 개발환경에선 전부 열어둡니다. CorsConfig 도 마찬가지
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
