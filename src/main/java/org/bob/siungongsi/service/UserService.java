package org.bob.siungongsi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bob.siungongsi.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.controller.dto.UserResponse.NotificationStatusResponse;
import org.bob.siungongsi.controller.dto.UserSubscriptionsResponse;
import org.bob.siungongsi.controller.dto.UserSubscriptionsResponse.SubscribedCompany;
import org.bob.siungongsi.domain.CompanyEntity;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.repository.CompanyRepository;
import org.bob.siungongsi.repository.NotificationRepository;
import org.bob.siungongsi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final CompanyRepository companyRepository;

  public UserService(
      UserRepository userRepository,
      NotificationRepository notificationRepository,
      CompanyRepository companyRepository) {
    this.userRepository = userRepository;
    this.notificationRepository = notificationRepository;
    this.companyRepository = companyRepository;
  }

  public NotificationStatusResponse getNotificationStatus() {
    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (userId == null) {
      throw new CustomException(ApiResponseCode.USER_REQUIRED_AUTHORIZATION);
    }

    Optional<UserEntity> userOpt = userRepository.findById(userId);
    if (!userOpt.isPresent()) {
      throw new CustomException(ApiResponseCode.AUTH_USER_NOT_FOUND);
    }

    UserEntity user = userOpt.get();
    // notiFlag 값을 Boolean으로 변환 (0이면 false, 1이면 true)
    boolean notificationEnabled = user.getNotiFlag();

    return NotificationStatusResponse.of(user.getId(), notificationEnabled);
  }

  @Transactional
  public NotificationStatusResponse updateNotificationStatus(UserNotificationRequest request) {
    if (request.notificationFlag() == null) {
      throw new CustomException(ApiResponseCode.USER_STATUS_ALREADY_EXIST);
    }

    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (userId == null) {
      throw new CustomException(ApiResponseCode.USER_REQUIRED_AUTHORIZATION);
    }

    Optional<UserEntity> userOpt = userRepository.findById(userId);
    if (!userOpt.isPresent()) {
      throw new CustomException(ApiResponseCode.AUTH_USER_NOT_FOUND);
    }

    UserEntity user = userOpt.get();

    // 알림 허용 여부 업데이트 (true -> 1, false -> 0)
    boolean notiFlag = request.notificationFlag();
    user.updateNotiFlag(notiFlag);

    // FCM 토큰 업데이트 (null이 아닌 경우에만)
    if (request.pushToken() != null && !request.pushToken().isBlank()) {
      user.updatePushTokenId(request.pushToken());
    }

    userRepository.save(user);

    return NotificationStatusResponse.of(user.getId(), request.notificationFlag());
  }

  public List<UserEntity> getUser() {
    return userRepository.findAll();
  }

  @Transactional(readOnly = true)
  public UserSubscriptionsResponse getUserSubscriptions() {
    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    List<Long> companyIds = notificationRepository.findCompanyIdsByUserId(userId);

    if (companyIds.isEmpty()) {
      return UserSubscriptionsResponse.of(userId, new ArrayList<>());
    }

    List<CompanyEntity> companies = companyRepository.findByIdIn(companyIds);

    List<SubscribedCompany> subscribedCompanies =
        companies.stream()
            .map(
                company ->
                    SubscribedCompany.of(
                        company.getId(),
                        company.getCompanyName(),
                        company.getCompanyCode(),
                        company.getStockCode()))
            .collect(Collectors.toList());

    return UserSubscriptionsResponse.of(userId, subscribedCompanies);
  }
}
