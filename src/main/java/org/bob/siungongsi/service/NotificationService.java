package org.bob.siungongsi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.bob.siungongsi.controller.dto.NotificationRequest;
import org.bob.siungongsi.controller.dto.NotificationResponse;
import org.bob.siungongsi.domain.CompanyEntity;
import org.bob.siungongsi.domain.NotiHistoryEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.CompanyRepository;
import org.bob.siungongsi.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final UserService userService;
  private final NotificationRepository notificationRepository;
  private final CompanyRepository companyRepository;

  public NotificationService(
      UserService userService,
      NotificationRepository notificationRepository,
      CompanyRepository companyRepository) {
    this.userService = userService;
    this.notificationRepository = notificationRepository;
    this.companyRepository = companyRepository;
  }

  public void createNotification(
      NotificationRequest.NotificationCompanyRequest notificationRequest) {
    // 인증된 유저의 ID 가져오기
    Long userId = userService.getAuthenticatedUserId();

    if (notificationRepository.existsByUserIdAndCompanyId(
        userId, notificationRequest.companyId())) {
      throw new CustomException(
          ApiResponseCode.NOTIFICATION_ALREADY_EXISTS,
          ApiResponseCode.NOTIFICATION_ALREADY_EXISTS.getMessage());
    }

    notificationRepository.save(new NotiHistoryEntity(userId, notificationRequest.companyId()));
  }

  public void deleteNotification(Long companyId) {
    Long userId = userService.getAuthenticatedUserId();
    if (notificationRepository.existsByUserIdAndCompanyId(userId, companyId)) {
      notificationRepository.deleteByUserIdAndCompanyId(companyId, userId);
    } else {
      throw new CustomException(ApiResponseCode.NOTIFICATION_NOT_FOUND, "존재하지 않는 알림 내역입니다.");
    }
  }

  public NotificationResponse.NotificationRecommendedCompanyList recommendedCompanyNotification() {
    List<Long> topCompanies = notificationRepository.findTop5Companies();

    if (topCompanies.size() < 5) {
      List<Long> additionalCompanies =
          companyRepository.findTop5ByOrderByCompanyNameAsc().stream()
              .map(CompanyEntity::getId)
              .filter(id -> !topCompanies.contains(id)) // 이미 포함된 기업은 제외
              .limit(5 - topCompanies.size()) // 부족한 개수만큼 추가
              .collect(Collectors.toList());
      topCompanies.addAll(additionalCompanies);
    }

    List<CompanyEntity> companies = companyRepository.findByIdIn(topCompanies);

    Long userId = userService.getAuthenticatedUserId();

    NotificationResponse.NotificationRecommendedCompanyList recommendedCompanies =
        NotificationResponse.NotificationRecommendedCompanyList.of(
            companies.stream()
                .map(
                    company -> {
                      boolean isSubscribed =
                          notificationRepository.existsByUserIdAndCompanyId(
                              userId, company.getId());
                      Long subscriberCnt = notificationRepository.countByCompanyId(company.getId());

                      return NotificationResponse.NotificationRecommendedCompany.of(
                          company.getId(), company.getCompanyName(), subscriberCnt, isSubscribed);
                    })
                .collect(Collectors.toList()));

    return recommendedCompanies;
  }
}
