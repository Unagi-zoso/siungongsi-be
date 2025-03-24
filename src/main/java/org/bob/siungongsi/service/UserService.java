package org.bob.siungongsi.service;

import java.util.Optional;

import org.bob.siungongsi.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.controller.dto.UserResponse.NotificationStatusResponse;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.NotificationRepository;
import org.bob.siungongsi.repository.UserAgreedTermRepository;
import org.bob.siungongsi.repository.UserRepository;
import org.bob.siungongsi.security.KakaoAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final KakaoAuthService kakaoAuthService;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final UserAgreedTermRepository userAgreedTermRepository;

  public UserService(
          KakaoAuthService kakaoAuthService,
          UserRepository userRepository,
          NotificationRepository notificationRepository,
          UserAgreedTermRepository userAgreedTermRepository) {
    this.kakaoAuthService = kakaoAuthService;
    this.userRepository = userRepository;
    this.notificationRepository = notificationRepository;
    this.userAgreedTermRepository = userAgreedTermRepository;
  }

  public Long getAuthenticatedUserId() {
    KakaoAuthenticationToken authentication =
            (KakaoAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      throw new CustomException(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION, "토큰이 없습니다.");
    }
    return kakaoAuthService.getUserId(authentication.getSocialId());
  }

  public NotificationStatusResponse getNotificationStatus() {
    Long userId = getAuthenticatedUserId();
    if (userId == null) {
      throw new CustomException(
              ApiResponseCode.USER_REQUIRED_AUTHORIZATION,
              ApiResponseCode.USER_REQUIRED_AUTHORIZATION.getMessage());
    }

    Optional<UserEntity> userOpt = userRepository.findById(userId);
    if (!userOpt.isPresent()) {
      throw new CustomException(
              ApiResponseCode.AUTH_USER_NOT_FOUND, ApiResponseCode.AUTH_USER_NOT_FOUND.getMessage());
    }

    UserEntity user = userOpt.get();
    // notiFlag 값을 Boolean으로 변환 (0이면 false, 1이면 true)
    boolean notificationEnabled = user.getNotiFlag() > 0;

    return NotificationStatusResponse.of(user.getId(), notificationEnabled);
  }

  @Transactional
  public NotificationStatusResponse updateNotificationStatus(UserNotificationRequest request) {
    if (request.notificationFlag() == null) {
      throw new CustomException(
              ApiResponseCode.USER_STATUS_ALREADY_EXIST,
              ApiResponseCode.USER_STATUS_ALREADY_EXIST.getMessage());
    }

    Long userId = getAuthenticatedUserId();
    if (userId == null) {
      throw new CustomException(
              ApiResponseCode.USER_REQUIRED_AUTHORIZATION,
              ApiResponseCode.USER_REQUIRED_AUTHORIZATION.getMessage());
    }

    Optional<UserEntity> userOpt = userRepository.findById(userId);
    if (!userOpt.isPresent()) {
      throw new CustomException(
              ApiResponseCode.AUTH_USER_NOT_FOUND, ApiResponseCode.AUTH_USER_NOT_FOUND.getMessage());
    }

    UserEntity user = userOpt.get();

    // 알림 허용 여부 업데이트 (true -> 1, false -> 0)
    Short notiFlag = request.notificationFlag() ? (short) 1 : (short) 0;
    user.updateNotiFlag(notiFlag);

    // FCM 토큰 업데이트 (null이 아닌 경우에만)
    if (request.pushToken() != null && !request.pushToken().isEmpty()) {
      user.updatePushTokenId(request.pushToken());
    }

    userRepository.save(user);

    return NotificationStatusResponse.of(user.getId(), request.notificationFlag());
  }
}
