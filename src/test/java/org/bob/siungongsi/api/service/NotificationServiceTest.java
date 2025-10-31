package org.bob.siungongsi.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_ID;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_ID_2;
import static org.bob.siungongsi.fixture.CompanyFixture.createMockedCompanies;
import static org.bob.siungongsi.fixture.NotificationFixture.MAX_NOTIFICATION_SUBSCRIPTIONS;
import static org.bob.siungongsi.fixture.UserFixture.TEST_USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.bob.siungongsi.api.controller.dto.NotificationRequest;
import org.bob.siungongsi.api.controller.dto.NotificationResponse;
import org.bob.siungongsi.common.domain.CompanyEntity;
import org.bob.siungongsi.common.domain.NotiHistoryEntity;
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
class NotificationServiceTest {

  @InjectMocks private NotificationService notificationService;

  @Mock private NotificationRepository notificationRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
  }

  /** createNotification */
  @Test
  @DisplayName("알림 구독 생성 시 NotiHistoryEntity를 반환한다")
  void whenCreateNotification_thenReturnsNotiHistoryEntity() {
    // given
    NotificationRequest.NotificationCompanyRequest request =
        new NotificationRequest.NotificationCompanyRequest(TEST_COMPANY_ID);
    NotiHistoryEntity expectedNotiHistory = mock(NotiHistoryEntity.class);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(companyRepository.existsById(TEST_COMPANY_ID)).thenReturn(true);
    when(userRepository.findNotiFlagById(TEST_USER_ID)).thenReturn(true);
    when(notificationRepository.insertIfUnderLimit(TEST_USER_ID, TEST_COMPANY_ID)).thenReturn(1);
    when(notificationRepository.findByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID))
        .thenReturn(Optional.of(expectedNotiHistory));

    // when
    NotiHistoryEntity result = notificationService.createNotification(request);

    // then
    assertThat(result).isEqualTo(expectedNotiHistory);
    verify(notificationRepository, times(1)).insertIfUnderLimit(TEST_USER_ID, TEST_COMPANY_ID);
  }

  @Test
  @DisplayName("존재하지 않는 사용자의 알림 구독 생성 시 예외가 발생한다")
  void whenCreateNotificationWithNonExistentUser_thenThrowsException() {
    // given
    NotificationRequest.NotificationCompanyRequest request =
        new NotificationRequest.NotificationCompanyRequest(TEST_COMPANY_ID);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> notificationService.createNotification(request))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.AUTH_USER_NOT_FOUND);
  }

  @Test
  @DisplayName("companyId가 null이면 예외가 발생한다")
  void whenCreateNotificationWithNullCompanyId_thenThrowsException() {
    // given
    NotificationRequest.NotificationCompanyRequest request =
        new NotificationRequest.NotificationCompanyRequest(null);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);

    // when & then
    assertThatThrownBy(() -> notificationService.createNotification(request))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.NOTIFICATION_COMPANY_ID_IS_NULL);
  }

  @Test
  @DisplayName("존재하지 않는 기업으로 알림 구독 생성 시 예외가 발생한다")
  void whenCreateNotificationWithInvalidCompanyId_thenThrowsException() {
    // given
    NotificationRequest.NotificationCompanyRequest request =
        new NotificationRequest.NotificationCompanyRequest(TEST_COMPANY_ID);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(companyRepository.existsById(TEST_COMPANY_ID)).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> notificationService.createNotification(request))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.NOTIFICATION_INVALID_COMPANY_ID);
  }

  @Test
  @DisplayName("알림 설정이 꺼져있으면 구독 생성 시 예외가 발생한다")
  void whenCreateNotificationWithNotiFlagOff_thenThrowsException() {
    // given
    NotificationRequest.NotificationCompanyRequest request =
        new NotificationRequest.NotificationCompanyRequest(TEST_COMPANY_ID);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(companyRepository.existsById(TEST_COMPANY_ID)).thenReturn(true);
    when(userRepository.findNotiFlagById(TEST_USER_ID)).thenReturn(false);

    // when & then
    assertThatThrownBy(() -> notificationService.createNotification(request))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.NOTIFICATION_REQUIRED_STATUS);
  }

  @Test
  @DisplayName("이미 구독 중인 기업에 대한 구독 생성 시 예외가 발생한다")
  void whenCreateNotificationWithExistingSubscription_thenThrowsException() {
    // given
    NotificationRequest.NotificationCompanyRequest request =
        new NotificationRequest.NotificationCompanyRequest(TEST_COMPANY_ID);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(companyRepository.existsById(TEST_COMPANY_ID)).thenReturn(true);
    when(userRepository.findNotiFlagById(TEST_USER_ID)).thenReturn(true);
    when(notificationRepository.insertIfUnderLimit(TEST_USER_ID, TEST_COMPANY_ID)).thenReturn(0);
    when(notificationRepository.existsByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID))
        .thenReturn(true);

    // when & then
    assertThatThrownBy(() -> notificationService.createNotification(request))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.NOTIFICATION_ALREADY_EXISTS);
  }

  @Test
  @DisplayName("구독 제한 초과 시 예외가 발생한다")
  void whenCreateNotificationExceedsLimit_thenThrowsException() {
    // given
    NotificationRequest.NotificationCompanyRequest request =
        new NotificationRequest.NotificationCompanyRequest(TEST_COMPANY_ID);

    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(companyRepository.existsById(TEST_COMPANY_ID)).thenReturn(true);
    when(userRepository.findNotiFlagById(TEST_USER_ID)).thenReturn(true);
    when(notificationRepository.insertIfUnderLimit(TEST_USER_ID, TEST_COMPANY_ID)).thenReturn(0);
    when(notificationRepository.existsByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID))
        .thenReturn(false);

    // when & then
    assertThatThrownBy(() -> notificationService.createNotification(request))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.NOTIFICATION_LIMIT_EXCEEDED);
  }

  /** deleteNotification */
  @Test
  @DisplayName("알림 구독 삭제 시 정상적으로 삭제된다")
  void whenDeleteNotification_thenDeletesSuccessfully() {
    // given
    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(notificationRepository.existsByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID))
        .thenReturn(true);
    when(companyRepository.existsById(TEST_COMPANY_ID)).thenReturn(true);
    when(userRepository.findNotiFlagById(TEST_USER_ID)).thenReturn(true);
    doNothing()
        .when(notificationRepository)
        .deleteByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID);

    // when
    notificationService.deleteNotification(TEST_COMPANY_ID);

    // then
    verify(notificationRepository, times(1))
        .deleteByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID);
  }

  @Test
  @DisplayName("존재하지 않는 구독 삭제 시 예외가 발생한다")
  void whenDeleteNonExistentNotification_thenThrowsException() {
    // given
    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(notificationRepository.existsByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID))
        .thenReturn(false);

    // when & then
    assertThatThrownBy(() -> notificationService.deleteNotification(TEST_COMPANY_ID))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.NOTIFICATION_NOT_FOUND);
  }

  /** recommendedCompanyNotification */
  @Test
  @DisplayName("추천 기업 목록 최대 5개를 조회한다")
  void whenRecommendedCompanyNotification_thenReturnsTop5Companies() {
    // given
    List<Long> topCompanyIds = new java.util.ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L));
    List<CompanyEntity> expectedCompanies = createMockedCompanies(5);

    when(notificationRepository.findTop5Companies()).thenReturn(topCompanyIds);
    when(companyRepository.findByIdIn(topCompanyIds)).thenReturn(expectedCompanies);
    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(notificationRepository.existsByUserIdAndCompanyId(anyLong(), anyLong())).thenReturn(false);
    when(notificationRepository.countByCompanyId(anyLong())).thenReturn(0L);

    // when
    NotificationResponse.NotificationRecommendedCompanyList result =
        notificationService.recommendedCompanyNotification();

    // then
    assertThat(result.companies()).hasSize(MAX_NOTIFICATION_SUBSCRIPTIONS);
  }

  @Test
  @DisplayName("추천 기업이 5개 미만이면 추가 기업으로 보충한다")
  void whenRecommendedCompanyNotificationLessThan5_thenFillsWithAdditionalCompanies() {
    // given
    List<Long> topCompanyIds = new java.util.ArrayList<>(List.of(1L, 2L));
    List<CompanyEntity> additionalCompanies =
        List.of(
            org.bob.siungongsi.fixture.CompanyFixture.mockedCompany().withId(3L).build(),
            org.bob.siungongsi.fixture.CompanyFixture.mockedCompany().withId(4L).build(),
            org.bob.siungongsi.fixture.CompanyFixture.mockedCompany().withId(5L).build());
    List<CompanyEntity> allCompanies = createMockedCompanies(5);

    when(notificationRepository.findTop5Companies()).thenReturn(topCompanyIds);
    when(companyRepository.findTop5ByOrderByCompanyNameAsc()).thenReturn(additionalCompanies);
    when(companyRepository.findByIdIn(any())).thenReturn(allCompanies);
    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(notificationRepository.existsByUserIdAndCompanyId(anyLong(), anyLong())).thenReturn(false);
    when(notificationRepository.countByCompanyId(anyLong())).thenReturn(0L);

    // when
    NotificationResponse.NotificationRecommendedCompanyList result =
        notificationService.recommendedCompanyNotification();

    // then
    assertThat(result.companies()).hasSize(MAX_NOTIFICATION_SUBSCRIPTIONS);
  }

  @Test
  @DisplayName("추천 기업 목록 조회 시 구독 여부를 확인한다")
  void whenRecommendedCompanyNotification_thenChecksSubscriptionStatus() {
    // given
    List<Long> topCompanyIds =
        new java.util.ArrayList<>(List.of(TEST_COMPANY_ID, TEST_COMPANY_ID_2));
    List<CompanyEntity> expectedCompanies = createMockedCompanies(2, TEST_COMPANY_ID);

    when(notificationRepository.findTop5Companies()).thenReturn(topCompanyIds);
    when(companyRepository.findTop5ByOrderByCompanyNameAsc()).thenReturn(createMockedCompanies(0));
    when(companyRepository.findByIdIn(any())).thenReturn(expectedCompanies);
    when(userRepository.existsById(TEST_USER_ID)).thenReturn(true);
    when(notificationRepository.existsByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID))
        .thenReturn(true);
    when(notificationRepository.existsByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID_2))
        .thenReturn(false);
    when(notificationRepository.countByCompanyId(anyLong())).thenReturn(0L);

    // when
    NotificationResponse.NotificationRecommendedCompanyList result =
        notificationService.recommendedCompanyNotification();

    // then
    assertThat(result.companies()).hasSize(expectedCompanies.size());
    assertThat(result.companies().get(0).isSubscribed()).isTrue();
    assertThat(result.companies().get(1).isSubscribed()).isFalse();
  }
}
