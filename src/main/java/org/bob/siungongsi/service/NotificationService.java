package org.bob.siungongsi.service;

import org.bob.siungongsi.controller.dto.NotificationRequest;
import org.bob.siungongsi.domain.NotiHistoryEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final UserService userService;
  private final NotificationRepository notificationRepository;

  public NotificationService(
      UserService userService, NotificationRepository notificationRepository) {
    this.userService = userService;
    this.notificationRepository = notificationRepository;
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
}
