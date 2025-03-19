package org.bob.siungongsi.service;

import org.bob.siungongsi.security.KakaoAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final KakaoAuthService kakaoAuthService;

  public UserService(KakaoAuthService kakaoAuthService) {
    this.kakaoAuthService = kakaoAuthService;
  }

  // 인증된 유저의 ID 가져오기
  public Long getAuthenticatedUserId() {
    // 인증된 토큰을 가져오기
    KakaoAuthenticationToken authentication =
        (KakaoAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      return null; // 인증되지 않은 경우 null 반환
    }

    // 인증된 사용자의 socialId를 가져오기
    String socialId = (String) authentication.getPrincipal();
    System.out.println("userId: " + kakaoAuthService.getUserId(socialId));
    return kakaoAuthService.getUserId(socialId); // socialId로 유저 ID 조회
  }
}
