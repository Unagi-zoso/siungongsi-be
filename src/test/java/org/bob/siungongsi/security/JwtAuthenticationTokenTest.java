package org.bob.siungongsi.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class JwtAuthenticationTokenTest {

  @Test
  @DisplayName("JwtAuthenticationToken 객체 생성 시 userId가 정상적으로 설정되어야 한다")
  void givenUserId_whenCreateJwtAuthenticationToken_thenUserIdShouldBeSet() {
    Long userId = 12345L;

    Authentication authentication = new JwtAuthenticationToken(userId);

    assertThat(authentication.getPrincipal()).isEqualTo(userId);
  }

  @Test
  @DisplayName("JwtAuthenticationToken의 getCredentials()는 항상 null을 반환해야 한다")
  void whenCallGetCredentials_thenShouldReturnNull() {
    JwtAuthenticationToken authentication = new JwtAuthenticationToken(12345L);

    Object credentials = authentication.getCredentials();

    assertThat(credentials).isNull();
  }

  @Test
  @DisplayName("JwtAuthenticationToken은 생성될 때 항상 인증되지 않은 상태여야 한다")
  void whenCreateJwtAuthenticationToken_thenShouldNotBeAuthenticated() {
    JwtAuthenticationToken authentication = new JwtAuthenticationToken(12345L);

    assertThat(authentication.isAuthenticated()).isFalse();
  }
}
