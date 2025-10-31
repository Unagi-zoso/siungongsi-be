package org.bob.siungongsi.fixture;

import java.util.List;
import java.util.stream.IntStream;

import org.bob.siungongsi.api.controller.dto.NotificationRequest.NotificationCompanyRequest;
import org.bob.siungongsi.api.controller.dto.NotificationResponse.NotificationRecommendedCompany;
import org.bob.siungongsi.api.controller.dto.NotificationResponse.NotificationRecommendedCompanyList;

public class NotificationFixture {

  // 비즈니스 제한
  public static final int MAX_NOTIFICATION_SUBSCRIPTIONS = 5;

  // FCM/Push
  public static final String TEST_FCM_TOKEN = "fcm-token-123";

  // 스케줄러 간격 (ms)
  public static final long MESSAGE_LISTENER_INTERVAL = 30000L; // 30초
  public static final long PUSH_WORKER_INTERVAL = 10000L; // 10초

  // 추천 회사 테스트 데이터
  public static final Long TEST_SUBSCRIBER_COUNT = 100L;
  public static final String TEST_RECOMMENDED_COMPANY_NAME = "추천기업A";
  public static final boolean IS_SUBSCRIBED = true;
  public static final boolean IS_NOT_SUBSCRIBED = false;

  // NotificationCompanyRequest 생성
  public static NotificationCompanyRequest createNotificationCompanyRequest(Long companyId) {
    return new NotificationCompanyRequest(companyId);
  }

  // NotificationRecommendedCompany 생성
  public static NotificationRecommendedCompany createRecommendedCompany(
      Long companyId, String companyName, Long subscriberCnt, boolean isSubscribed) {
    return NotificationRecommendedCompany.of(companyId, companyName, subscriberCnt, isSubscribed);
  }

  // 기본 추천 회사 생성
  public static NotificationRecommendedCompany createDefaultRecommendedCompany() {
    return createRecommendedCompany(
        1L, TEST_RECOMMENDED_COMPANY_NAME, TEST_SUBSCRIBER_COUNT, IS_NOT_SUBSCRIBED);
  }

  // 추천 회사 리스트 생성
  public static NotificationRecommendedCompanyList createRecommendedCompanyList(int size) {
    List<NotificationRecommendedCompany> companies =
        IntStream.range(0, size)
            .mapToObj(
                i ->
                    createRecommendedCompany(
                        (long) (i + 1), "추천기업" + (i + 1), TEST_SUBSCRIBER_COUNT + i, i % 2 == 0))
            .toList();
    return NotificationRecommendedCompanyList.of(companies);
  }

  // 빈 추천 회사 리스트 생성
  public static NotificationRecommendedCompanyList createEmptyRecommendedCompanyList() {
    return NotificationRecommendedCompanyList.of(List.of());
  }
}
