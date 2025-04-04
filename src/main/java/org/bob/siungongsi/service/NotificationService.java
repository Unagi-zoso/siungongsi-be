package org.bob.siungongsi.service;

import java.util.List;
import java.util.Optional;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;

  public NotificationService(
      NotificationRepository notificationRepository,
      CompanyRepository companyRepository,
      UserRepository userRepository) {
    this.notificationRepository = notificationRepository;
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public NotiHistoryEntity createNotification(
      NotificationRequest.NotificationCompanyRequest notificationRequest) {
    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // 기본 검증
    if (!userRepository.findById(userId).isPresent()) {
      throw new CustomException(ApiResponseCode.AUTH_USER_NOT_FOUND);
    }

    if (!companyRepository.existsById(notificationRequest.companyId())) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_INVALID_COMPANY_ID);
    }

    if (!userRepository.findNotiFlagById(userId)) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_REQUIRED_STATUS);
    }

    // 원자적 쿼리 실행 - 제한 체크와 삽입을 하나의 쿼리로 수행
    int inserted =
        notificationRepository.insertIfUnderLimit(userId, notificationRequest.companyId());

    if (inserted == 0) {
      // 삽입 실패: 제한 초과 또는 이미 존재함
      if (notificationRepository.existsByUserIdAndCompanyId(
          userId, notificationRequest.companyId())) {
        throw new CustomException(ApiResponseCode.NOTIFICATION_ALREADY_EXISTS);
      } else {
        throw new CustomException(ApiResponseCode.NOTIFICATION_LIMIT_EXCEEDED);
      }
    }

    Optional<NotiHistoryEntity> entity =
        notificationRepository.findByUserIdAndCompanyId(userId, notificationRequest.companyId());

    return entity.orElseThrow(
        () -> new RuntimeException("Failed to retrieve created notification"));
  }

  @Transactional
  public void deleteNotification(Long companyId) {
    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (!notificationRepository.existsByUserIdAndCompanyId(userId, companyId)) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_NOT_FOUND);
    }

    if (!companyRepository.existsById(companyId)) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_INVALID_COMPANY_ID);
    }

    if (!userRepository.findNotiFlagById(userId)) {
      throw new CustomException(ApiResponseCode.NOTIFICATION_REQUIRED_STATUS);
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

    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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
