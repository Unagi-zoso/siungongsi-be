package org.bob.siungongsi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("dev")
@Configuration
class CorsConfigForDev {

  private final CorsProperties corsProperties;

  public CorsConfigForDev(CorsProperties corsProperties) {
    this.corsProperties = corsProperties;
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**") // 모든 엔드포인트에 대해 CORS 허용
            .allowedOrigins("*") // 모든 도메인 허용
            .allowedMethods("*") // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
            .allowedHeaders("*") // 모든 요청 헤더 허용
            .allowCredentials(false); // 쿠키 포함 허용 (true 시 allowedOrigins에 * 사용 불가)
      }
    };
  }
}
