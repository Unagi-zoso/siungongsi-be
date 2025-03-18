package org.bob.siungongsi.security;

import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class KakaoAuthenticationToken extends UsernamePasswordAuthenticationToken {

  private final String socialId;

  public KakaoAuthenticationToken(String socialId) {
    super(socialId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    this.socialId = socialId;
  }

  public String getSocialId() {
    return socialId;
  }
}
