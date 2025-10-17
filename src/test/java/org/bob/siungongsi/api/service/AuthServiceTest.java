package org.bob.siungongsi.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.bob.siungongsi.fixture.AuthFixture.ALL_REQUIRED_TERM_IDS;
import static org.bob.siungongsi.fixture.AuthFixture.ALL_TERM_IDS;
import static org.bob.siungongsi.fixture.AuthFixture.OPTIONAL_TERM_ID;
import static org.bob.siungongsi.fixture.AuthFixture.REQUIRED_TERM_ID_1;
import static org.bob.siungongsi.fixture.AuthFixture.REQUIRED_TERM_ID_2;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_BEARER_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_JWT_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_KAKAO_ACCESS_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_SOCIAL_ID;
import static org.bob.siungongsi.fixture.AuthFixture.createValidRegisterRequest;
import static org.bob.siungongsi.fixture.TermFixture.createAllTermEntities;
import static org.bob.siungongsi.fixture.UserFixture.TEST_USER_ID;
import static org.bob.siungongsi.fixture.UserFixture.createMockedUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.bob.siungongsi.api.controller.dto.AuthRequest;
import org.bob.siungongsi.api.controller.dto.AuthResponse;
import org.bob.siungongsi.api.controller.dto.TermsResponse;
import org.bob.siungongsi.common.domain.TermEntity;
import org.bob.siungongsi.common.domain.UserAgreedTermEntity;
import org.bob.siungongsi.common.domain.UserEntity;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.repository.NotificationRepository;
import org.bob.siungongsi.common.repository.TermRepository;
import org.bob.siungongsi.common.repository.UserAgreedTermRepository;
import org.bob.siungongsi.common.repository.UserRepository;
import org.bob.siungongsi.common.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @InjectMocks private AuthService authService;

  @Mock private TermRepository termRepository;
  @Mock private UserRepository userRepository;
  @Mock private UserAgreedTermRepository userAgreedTermRepository;
  @Mock private KakaoAuthService kakaoAuthService;
  @Mock private NotificationRepository notificationRepository;
  @Mock private JwtProvider jwtProvider;
  @Mock private AuthBlackListService authBlackListService;

  /** register */
  @Test
  @DisplayName("회원가입 시 JWT 토큰을 반환한다")
  void whenRegister_thenReturnsJwtToken() {
    // given
    AuthRequest.RegisterRequest request = createValidRegisterRequest();
    UserEntity user = createMockedUser(TEST_USER_ID);

    when(kakaoAuthService.getSocialIdFromAccessToken(TEST_BEARER_TOKEN)).thenReturn(TEST_SOCIAL_ID);
    when(userRepository.existsBySocialId(TEST_SOCIAL_ID)).thenReturn(false);
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);
    when(termRepository.findIdsByRequiredFlag()).thenReturn(ALL_REQUIRED_TERM_IDS);
    when(termRepository.existsById(anyLong())).thenReturn(true);
    when(userAgreedTermRepository.existsByUserIdAndTermId(anyLong(), anyLong())).thenReturn(false);
    when(userAgreedTermRepository.saveAll(any())).thenReturn(List.of());
    when(jwtProvider.createJwtToken(anyString())).thenReturn(TEST_JWT_TOKEN);

    // when
    String result = authService.register(request, TEST_BEARER_TOKEN);

    // then
    assertThat(result).isEqualTo(TEST_JWT_TOKEN);
  }

  @Test
  @DisplayName("Authorization 헤더가 없으면 예외가 발생한다")
  void whenRegisterWithoutAuthorization_thenThrowsException() {
    // given
    AuthRequest.RegisterRequest request = createValidRegisterRequest();

    // when & then
    assertThatThrownBy(() -> authService.register(request, null))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION);
  }

  @Test
  @DisplayName("이미 존재하는 사용자로 회원가입 시 예외가 발생한다")
  void whenRegisterExistingUser_thenThrowsException() {
    // given
    AuthRequest.RegisterRequest request = createValidRegisterRequest();

    when(kakaoAuthService.getSocialIdFromAccessToken(TEST_BEARER_TOKEN)).thenReturn(TEST_SOCIAL_ID);
    when(userRepository.existsBySocialId(TEST_SOCIAL_ID)).thenReturn(true);

    // when & then
    assertThatThrownBy(() -> authService.register(request, TEST_BEARER_TOKEN))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_USER_ALREADY_EXISTS);
  }

  @Test
  @DisplayName("필수 약관 미동의 시 예외가 발생한다")
  void whenRegisterWithoutRequiredTerms_thenThrowsException() {
    // given
    AuthRequest.RegisterRequest request =
        new AuthRequest.RegisterRequest(List.of(OPTIONAL_TERM_ID));
    UserEntity user = createMockedUser(TEST_USER_ID);

    when(kakaoAuthService.getSocialIdFromAccessToken(TEST_BEARER_TOKEN)).thenReturn(TEST_SOCIAL_ID);
    when(userRepository.existsBySocialId(TEST_SOCIAL_ID)).thenReturn(false);
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);
    when(termRepository.findIdsByRequiredFlag()).thenReturn(ALL_REQUIRED_TERM_IDS);

    // when & then
    assertThatThrownBy(() -> authService.register(request, TEST_BEARER_TOKEN))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue(
            "errorCode", ApiResponseCode.AUTH_REQUIRED_TERMS_NOT_AGREED);
  }

  @Test
  @DisplayName("존재하지 않는 약관 ID로 회원가입 시 예외가 발생한다")
  void whenRegisterWithInvalidTermId_thenThrowsException() {
    // given
    AuthRequest.RegisterRequest request = createValidRegisterRequest();
    UserEntity user = createMockedUser(TEST_USER_ID);

    when(kakaoAuthService.getSocialIdFromAccessToken(TEST_BEARER_TOKEN)).thenReturn(TEST_SOCIAL_ID);
    when(userRepository.existsBySocialId(TEST_SOCIAL_ID)).thenReturn(false);
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);
    when(termRepository.findIdsByRequiredFlag()).thenReturn(ALL_REQUIRED_TERM_IDS);
    when(termRepository.existsById(anyLong())).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> authService.register(request, TEST_BEARER_TOKEN))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_TERMS_ID_NOT_FOUND);
  }

  /** login */
  @Test
  @DisplayName("기존 사용자 로그인 시 JWT 토큰과 isUser가 true를 반환한다")
  void whenLoginExistingUser_thenReturnsJwtTokenAndIsUserTrue() {
    // given
    UserEntity user = createMockedUser(TEST_USER_ID);

    when(kakaoAuthService.getSocialIdFromAccessToken(TEST_BEARER_TOKEN)).thenReturn(TEST_SOCIAL_ID);
    when(userRepository.findBySocialId(TEST_SOCIAL_ID)).thenReturn(Optional.of(user));
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);
    when(jwtProvider.createJwtToken(anyString())).thenReturn(TEST_JWT_TOKEN);

    // when
    AuthResponse.LoginSuccessResponse result = authService.login(TEST_BEARER_TOKEN);

    // then
    assertThat(result.accessToken()).isEqualTo(TEST_JWT_TOKEN);
    assertThat(result.isUser()).isTrue();
  }

  @Test
  @DisplayName("신규 사용자 로그인 시 null 토큰과 isUser가 false를 반환한다")
  void whenLoginNewUser_thenReturnsNullTokenAndIsUserFalse() {
    // given
    when(kakaoAuthService.getSocialIdFromAccessToken(TEST_BEARER_TOKEN)).thenReturn(TEST_SOCIAL_ID);
    when(userRepository.findBySocialId(TEST_SOCIAL_ID)).thenReturn(Optional.empty());

    // when
    AuthResponse.LoginSuccessResponse result = authService.login(TEST_BEARER_TOKEN);

    // then
    assertThat(result.accessToken()).isNull();
    assertThat(result.isUser()).isFalse();
  }

  /** getTerms */
  @Test
  @DisplayName("약관 조회 시 약관 목록을 반환한다")
  void whenGetTerms_thenReturnsTermsList() {
    // given
    List<TermEntity> termEntities = createAllTermEntities();

    when(termRepository.findAll()).thenReturn(termEntities);

    // when
    List<TermsResponse> result = authService.getTerms();

    // then
    assertThat(result).hasSize(termEntities.size());
  }

  @Test
  @DisplayName("약관이 없을 때 예외가 발생한다")
  void whenGetTermsWithNoTerms_thenThrowsException() {
    // given
    when(termRepository.findAll()).thenReturn(List.of());

    // when & then
    assertThatThrownBy(() -> authService.getTerms())
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_TERMS_NOT_FOUND);
  }

  /** logout */
  @Test
  @DisplayName("로그아웃 시 블랙리스트에 토큰이 추가된다")
  void whenLogout_thenTokenIsAddedToBlackList() {
    // given
    String bearerToken = "Bearer " + TEST_JWT_TOKEN;
    long remainingTime = 3600000L;

    when(jwtProvider.getRemainingExpirationTime(bearerToken)).thenReturn(remainingTime);
    doNothing().when(authBlackListService).setBlackList(bearerToken, "logout", remainingTime);

    // when
    authService.logout(bearerToken);

    // then
    verify(authBlackListService).setBlackList(bearerToken, "logout", remainingTime);
  }

  /** withdrawUser */
  @Test
  @DisplayName("회원탈퇴 시 사용자 정보가 삭제된다")
  void whenWithdrawUser_thenUserIsDeleted() {
    // given
    SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
    Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    doNothing().when(notificationRepository).deleteAllByUserId(TEST_USER_ID);
    doNothing().when(userAgreedTermRepository).deleteAllByUserId(TEST_USER_ID);
    doNothing().when(userRepository).deleteById(TEST_USER_ID);

    // when
    authService.withdrawUser();

    // then
    verify(userRepository).deleteById(TEST_USER_ID);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 회원탈퇴 시 예외가 발생한다")
  void whenWithdrawNonExistentUser_thenThrowsException() {
    // given
    SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
    Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> authService.withdrawUser())
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_USER_NOT_FOUND);
  }

  /** createJwt */
  @Test
  @DisplayName("JWT 토큰을 생성한다")
  void whenCreateJwt_thenReturnsJwtToken() {
    // given
    String userId = TEST_USER_ID.toString();

    when(jwtProvider.createJwtToken(userId)).thenReturn(TEST_JWT_TOKEN);

    // when
    String result = authService.createJwt(userId);

    // then
    assertThat(result).isEqualTo(TEST_JWT_TOKEN);
  }
}
