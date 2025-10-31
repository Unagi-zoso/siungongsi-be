package org.bob.siungongsi.fixture;

import static org.bob.siungongsi.fixture.AuthFixture.TEST_KAKAO_ACCESS_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_SOCIAL_ID;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_CODE;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_ID;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_NAME;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_STOCK_CODE;
import static org.bob.siungongsi.fixture.NotificationFixture.TEST_FCM_TOKEN;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;

import org.bob.siungongsi.api.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.api.controller.dto.UserResponse.NotificationStatusResponse;
import org.bob.siungongsi.api.controller.dto.UserSubscriptionsResponse;
import org.bob.siungongsi.api.controller.dto.UserSubscriptionsResponse.SubscribedCompany;
import org.bob.siungongsi.common.domain.UserEntity;

public class UserFixture {

  // 테스트용 사용자 ID 상수
  public static final Long TEST_USER_ID = 1L;
  public static final Long TEST_USER_ID_2 = 2L;

  public static class UserEntityBuilder {
    private String socialId = TEST_SOCIAL_ID;
    private String accessToken = TEST_KAKAO_ACCESS_TOKEN;
    private String pushTokenId = null;
    private boolean notiFlag = false;

    public UserEntityBuilder socialId(String socialId) {
      this.socialId = socialId;
      return this;
    }

    public UserEntityBuilder accessToken(String accessToken) {
      this.accessToken = accessToken;
      return this;
    }

    public UserEntityBuilder pushTokenId(String pushTokenId) {
      this.pushTokenId = pushTokenId;
      return this;
    }

    public UserEntityBuilder notiFlag(boolean notiFlag) {
      this.notiFlag = notiFlag;
      return this;
    }

    public UserEntity build() {
      UserEntity user = new UserEntity(socialId, accessToken);
      if (pushTokenId != null) {
        user.updatePushTokenId(pushTokenId);
      }
      user.updateNotiFlag(notiFlag);
      return user;
    }
  }

  public static UserEntityBuilder userEntity() {
    return new UserEntityBuilder();
  }

  /** Service 단위 테스트용 Mock UserEntity Builder */
  public static class MockedUserEntityBuilder {
    private final UserEntity user = mock(UserEntity.class);

    public MockedUserEntityBuilder withId(Long id) {
      when(user.getId()).thenReturn(id);
      return this;
    }

    public MockedUserEntityBuilder withSocialId(String socialId) {
      when(user.getSocialId()).thenReturn(socialId);
      return this;
    }

    public MockedUserEntityBuilder withAccessToken(String accessToken) {
      when(user.getAccessToken()).thenReturn(accessToken);
      return this;
    }

    public MockedUserEntityBuilder withPushTokenId(String pushTokenId) {
      when(user.getPushTokenId()).thenReturn(pushTokenId);
      return this;
    }

    public MockedUserEntityBuilder withNotiFlag(boolean notiFlag) {
      when(user.getNotiFlag()).thenReturn(notiFlag);
      return this;
    }

    public UserEntity build() {
      return user;
    }
  }

  public static MockedUserEntityBuilder mockedUser() {
    return new MockedUserEntityBuilder();
  }

  // 단건 생성 편의 메서드 (Repository/Integration 테스트용)
  public static UserEntity createUser(String socialId, String accessToken) {
    return userEntity().socialId(socialId).accessToken(accessToken).build();
  }

  // 다건 생성 편의 메서드
  public static List<UserEntity> createUsers(int size) {
    return IntStream.range(0, size)
        .mapToObj(i -> createUser(TEST_SOCIAL_ID + i, TEST_KAKAO_ACCESS_TOKEN + "-" + i))
        .toList();
  }

  // UserNotificationRequest 생성
  public static UserNotificationRequest createUserNotificationRequest(
      Boolean notificationFlag, String pushToken) {
    return UserNotificationRequest.of(notificationFlag, pushToken);
  }

  public static UserNotificationRequest createUserNotificationRequestWithFlag(
      Boolean notificationFlag) {
    return UserNotificationRequest.of(notificationFlag, TEST_FCM_TOKEN);
  }

  // NotificationStatusResponse 생성
  public static NotificationStatusResponse createNotificationStatusResponse(
      Long userId, Boolean notificationFlag) {
    return NotificationStatusResponse.of(userId, notificationFlag);
  }

  public static NotificationStatusResponse createDefaultNotificationStatusResponse() {
    return NotificationStatusResponse.of(TEST_USER_ID, true);
  }

  // UserSubscriptionsResponse 생성
  public static UserSubscriptionsResponse createUserSubscriptionsResponse(
      Long userId, List<SubscribedCompany> subscribedCompanies) {
    return UserSubscriptionsResponse.of(userId, subscribedCompanies);
  }

  public static UserSubscriptionsResponse createEmptyUserSubscriptionsResponse() {
    return UserSubscriptionsResponse.of(TEST_USER_ID, List.of());
  }

  public static UserSubscriptionsResponse createUserSubscriptionsResponseWithSize(int size) {
    List<SubscribedCompany> companies =
        IntStream.range(0, size)
            .mapToObj(
                i ->
                    SubscribedCompany.of(
                        (long) (i + 1),
                        TEST_COMPANY_NAME + i,
                        TEST_COMPANY_CODE + i,
                        TEST_STOCK_CODE + i))
            .toList();
    return UserSubscriptionsResponse.of(TEST_USER_ID, companies);
  }

  // SubscribedCompany 생성
  public static SubscribedCompany createSubscribedCompany(
      Long companyId, String companyName, String companyCode, String stockCode) {
    return SubscribedCompany.of(companyId, companyName, companyCode, stockCode);
  }

  public static SubscribedCompany createDefaultSubscribedCompany() {
    return SubscribedCompany.of(
        TEST_COMPANY_ID, TEST_COMPANY_NAME, TEST_COMPANY_CODE, TEST_STOCK_CODE);
  }
}
