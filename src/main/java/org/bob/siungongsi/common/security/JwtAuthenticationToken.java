package org.bob.siungongsi.common.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final Long userId;

  public JwtAuthenticationToken(Long userId) {
    super(null);
    this.userId = userId;
    setAuthenticated(false);
  }

  @Override
  public Object getPrincipal() {
    return userId;
  }

  @Override
  public Object getCredentials() {
    return null;
  }
}
