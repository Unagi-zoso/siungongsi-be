package org.bob.siungongsi.config;

import org.bob.siungongsi.security.ExceptionHandlerFilter;
import org.bob.siungongsi.security.JwtAuthFilter;
import org.bob.siungongsi.security.JwtProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtProvider jwtProvider;

  public SecurityConfig(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(
        (authorizeRequests) -> {
          authorizeRequests.anyRequest().permitAll();
        });

    http.cors(AbstractHttpConfigurer::disable);
    http.csrf(AbstractHttpConfigurer::disable);

    http.addFilterBefore(
        new JwtAuthFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(new ExceptionHandlerFilter(), JwtAuthFilter.class);
    return http.build();
  }
}
