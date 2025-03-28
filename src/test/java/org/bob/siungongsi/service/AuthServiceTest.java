package org.bob.siungongsi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.NotificationRepository;
import org.bob.siungongsi.repository.TermRepository;
import org.bob.siungongsi.repository.UserAgreedTermRepository;
import org.bob.siungongsi.repository.UserRepository;
import org.bob.siungongsi.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(AuthService.class)
class AuthServiceTest {

  @MockitoBean private TermRepository termRepository;
  @MockitoBean private UserRepository userRepository;
  @MockitoBean private UserAgreedTermRepository userAgreedTermRepository;
  @MockitoBean private KakaoAuthService kakaoAuthService;
  @MockitoBean private NotificationRepository notificationRepository;
  @MockitoBean private JwtProvider jwtProvider;

  @Autowired private AuthService authService;

  @Test
  void testRegister_Success() {
    // Arrange
    String accessToken = "Bearer validToken";
    String socialId = "kakao123";
    AuthRequest.RegisterRequest registerRequest = mock(AuthRequest.RegisterRequest.class);
    when(registerRequest.agreedTermIds()).thenReturn(Arrays.asList(1L, 2L));

    when(kakaoAuthService.getSocialIdFromAccessToken(accessToken)).thenReturn(socialId);
    when(userRepository.existsBySocialId(socialId)).thenReturn(false);

    // 대신 when 절에서 직접 ID 설정
    UserEntity mockUser = new UserEntity(socialId, "validToken");
    when(mockUser.getId()).thenReturn(1L);
    when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

    when(termRepository.findIdsByRequiredFlag()).thenReturn(Collections.singletonList(1L));
    when(termRepository.existsById(any())).thenReturn(true);
    when(userAgreedTermRepository.existsByUserIdAndTermId(any(), any())).thenReturn(false);

    when(jwtProvider.createJwtToken(anyString())).thenReturn("jwtToken");

    // Act
    String resultJwt = authService.register(registerRequest, accessToken);

    // Assert
    assertNotNull(resultJwt);
    assertEquals("jwtToken", resultJwt);

    verify(userRepository).save(any(UserEntity.class));
    verify(userAgreedTermRepository).saveAll(anyList());
  }

  @Test
  @DisplayName("이미 가입된 사용자 예외")
  void givenExistingUser_whenRegister_thenThrowsException() {
    // given
    String accessToken = "Bearer testAccessToken";
    String socialId = "mockSocialId";
    AuthRequest.RegisterRequest request = new AuthRequest.RegisterRequest(List.of(1L, 2L));

    when(kakaoAuthService.getSocialIdFromAccessToken(accessToken)).thenReturn(socialId);
    when(userRepository.existsBySocialId(socialId)).thenReturn(true);

    // when & then
    CustomException exception =
        assertThrows(CustomException.class, () -> authService.register(request, accessToken));
    assertEquals(ApiResponseCode.AUTH_USER_ALREADY_EXISTS, exception.getErrorCode());
  }
}
