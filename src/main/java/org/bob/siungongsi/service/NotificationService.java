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
import org.bob.siungongsi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class NotificationService {

  private final UserService userService;
  private final NotificationRepository notificationRepository;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;

  public NotificationService(
      UserService userService,
      NotificationRepository notificationRepository,
      CompanyRepository companyRepository,
      UserRepository userRepository) {
    this.userService = userService;
    this.notificationRepository = notificationRepository;
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
  }

  public NotiHistoryEntity createNotification(
      NotificationRequest.NotificationCompanyRequest notificationRequest) {
    // 인증된 유저의 ID 가져오기
    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (notificationRepository.existsByUserIdAndCompanyId(
        userId, notificationRequest.companyId())) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_ALREADY_EXISTS, "이미 존재하는 알림입니다.");
    }

    if (!companyRepository.existsById(notificationRequest.companyId())) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_INVALID_COMPANY_ID, "존재하지 않는 기업입니다.");
    }

    if (userRepository.findNotiFlagById(userId) == 0) {
      throw new CustomException(
          ApiResponseCode.NOTIFICATION_REQUIRED_STATUS, "유저가 알림을 동의하지 않았습니다. ");
    }

    return notificationRepository.save(
        new NotiHistoryEntity(userId, notificationRequest.companyId()));
  }

  @Transactional
  public void deleteNotification(Long companyId) {
    Long userId = userService.getAuthenticatedUserId();

    if (!notificationRepository.existsByUserIdAndCompanyId(userId, companyId)) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_NOT_FOUND, "존재하지 않는 알림 내역입니다.");
    }

    if (!companyRepository.existsById(companyId)) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_INVALID_COMPANY_ID, "존재하지 않는 기업입니다.");
    }

    if (userRepository.findNotiFlagById(userId) == 0) {
      throw new CustomException(
          ApiResponseCode.NOTIFICATION_REQUIRED_STATUS, "유저가 알림을 동의하지 않았습니다. ");
    }

    notificationRepository.deleteByUserIdAndCompanyId(userId, companyId);
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
