package org.bob.siungongsi.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_NAME;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_NAME_2;
import static org.bob.siungongsi.fixture.CompanyFixture.mockedCompany;
import static org.bob.siungongsi.fixture.NotificationFixture.TEST_FCM_TOKEN;
import static org.bob.siungongsi.fixture.UserFixture.TEST_USER_ID;
import static org.bob.siungongsi.fixture.UserFixture.mockedUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.bob.siungongsi.api.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.api.controller.dto.UserResponse.NotificationStatusResponse;
import org.bob.siungongsi.api.controller.dto.UserSubscriptionsResponse;
import org.bob.siungongsi.common.domain.CompanyEntity;
import org.bob.siungongsi.common.domain.NotiHistoryEntity;
import org.bob.siungongsi.common.domain.UserEntity;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.repository.CompanyRepository;
import org.bob.siungongsi.common.repository.NotificationRepository;
import org.bob.siungongsi.common.repository.UserRepository;
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
class UserServiceTest {

  @InjectMocks private UserService userService;

  @Mock private UserRepository userRepository;
  @Mock private NotificationRepository notificationRepository;
  @Mock private CompanyRepository companyRepository;

  private SecurityContext securityContext;
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    securityContext = mock(SecurityContext.class);
    authentication = mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);
  }

  /** getNotificationStatus */
  @Test
  @DisplayName("알림 상태 조회 시 사용자의 알림 설정을 반환한다")
  void whenGetNotificationStatus_thenReturnsUserNotificationFlag() {
    // given
    UserEntity user = mockedUser().withId(TEST_USER_ID).withNotiFlag(true).build();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

    // when
    NotificationStatusResponse result = userService.getNotificationStatus();

    // then
    assertThat(result.userId()).isEqualTo(TEST_USER_ID);
    assertThat(result.notificationFlag()).isTrue();
  }

  @Test
  @DisplayName("인증되지 않은 사용자가 알림 상태 조회 시 예외가 발생한다")
  void whenGetNotificationStatusWithoutAuth_thenThrowsException() {
    // given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(null);

    // when & then
    assertThatThrownBy(() -> userService.getNotificationStatus())
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.USER_REQUIRED_AUTHORIZATION);
  }

  @Test
  @DisplayName("존재하지 않는 사용자의 알림 상태 조회 시 예외가 발생한다")
  void whenGetNotificationStatusWithNonExistentUser_thenThrowsException() {
    // given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.getNotificationStatus())
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_USER_NOT_FOUND);
  }

  /** updateNotificationStatus */
  @Test
  @DisplayName("알림 설정 업데이트 시 notiFlag와 pushToken이 변경된다")
  void whenUpdateNotificationStatus_thenUpdatesNotiFlagAndPushToken() {
    // given
    UserNotificationRequest request = new UserNotificationRequest(true, TEST_FCM_TOKEN);
    UserEntity user = mockedUser().withId(TEST_USER_ID).build();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);

    // when
    NotificationStatusResponse result = userService.updateNotificationStatus(request);

    // then
    assertThat(result.userId()).isEqualTo(TEST_USER_ID);
    assertThat(result.notificationFlag()).isTrue();
    verify(user, times(1)).updateNotiFlag(true);
    verify(user, times(1)).updatePushTokenId(TEST_FCM_TOKEN);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  @DisplayName("pushToken 없이 알림 설정 업데이트 시 notiFlag만 변경된다")
  void whenUpdateNotificationStatusWithoutPushToken_thenUpdatesOnlyNotiFlag() {
    // given
    UserNotificationRequest request = new UserNotificationRequest(false, null);
    UserEntity user = mockedUser().withId(TEST_USER_ID).build();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
    when(userRepository.save(any(UserEntity.class))).thenReturn(user);

    // when
    NotificationStatusResponse result = userService.updateNotificationStatus(request);

    // then
    assertThat(result.notificationFlag()).isFalse();
    verify(user, times(1)).updateNotiFlag(false);
    verify(user, times(0)).updatePushTokenId(any());
  }

  @Test
  @DisplayName("notificationFlag가 null이면 예외가 발생한다")
  void whenUpdateNotificationStatusWithNullFlag_thenThrowsException() {
    // given
    UserNotificationRequest request = new UserNotificationRequest(null, TEST_FCM_TOKEN);

    // when & then
    assertThatThrownBy(() -> userService.updateNotificationStatus(request))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.USER_STATUS_ALREADY_EXIST);
  }

  /** getUser */
  @Test
  @DisplayName("전체 사용자 목록을 조회한다")
  void whenGetUser_thenReturnsAllUsers() {
    // given
    List<UserEntity> expectedUsers =
        List.of(mockedUser().build(), mockedUser().build(), mockedUser().build());

    when(userRepository.findAll()).thenReturn(expectedUsers);

    // when
    List<UserEntity> result = userService.getUser();

    // then
    assertThat(result).hasSize(expectedUsers.size());
    verify(userRepository, times(1)).findAll();
  }

  /** getUserSubscriptions */
  @Test
  @DisplayName("사용자의 구독 목록을 조회한다")
  void whenGetUserSubscriptions_thenReturnsSubscribedCompanies() {
    // given
    NotiHistoryEntity noti1 = mock(NotiHistoryEntity.class);
    NotiHistoryEntity noti2 = mock(NotiHistoryEntity.class);
    when(noti1.getCompanyId()).thenReturn(1L);
    when(noti2.getCompanyId()).thenReturn(2L);

    CompanyEntity company1 = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    CompanyEntity company2 = mockedCompany().withCompanyName(TEST_COMPANY_NAME_2).build();
    List<CompanyEntity> expectedCompanies = List.of(company1, company2);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(notificationRepository.findByUserId(TEST_USER_ID)).thenReturn(List.of(noti1, noti2));
    when(companyRepository.findByIdIn(List.of(1L, 2L))).thenReturn(expectedCompanies);

    // when
    UserSubscriptionsResponse result = userService.getUserSubscriptions();

    // then
    assertThat(result.userId()).isEqualTo(TEST_USER_ID);
    assertThat(result.subscribedCompanies()).hasSize(expectedCompanies.size());
    assertThat(result.subscribedCompanies().get(0).companyName()).isEqualTo(TEST_COMPANY_NAME);
    assertThat(result.subscribedCompanies().get(1).companyName()).isEqualTo(TEST_COMPANY_NAME_2);
  }

  @Test
  @DisplayName("구독이 없는 사용자는 빈 리스트를 반환한다")
  void whenGetUserSubscriptionsWithNoSubscriptions_thenReturnsEmptyList() {
    // given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(notificationRepository.findByUserId(TEST_USER_ID)).thenReturn(List.of());

    // when
    UserSubscriptionsResponse result = userService.getUserSubscriptions();

    // then
    assertThat(result.userId()).isEqualTo(TEST_USER_ID);
    assertThat(result.subscribedCompanies()).isEmpty();
    verify(companyRepository, times(0)).findByIdIn(any());
  }
}
