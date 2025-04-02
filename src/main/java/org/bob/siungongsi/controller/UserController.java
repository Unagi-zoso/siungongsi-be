package org.bob.siungongsi.controller;

import static org.bob.siungongsi.dto.ApiResponseCode.*;

import org.bob.siungongsi.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.controller.dto.UserResponse.NotificationStatusResponse;
import org.bob.siungongsi.controller.dto.UserSubscriptionsResponse;
import org.bob.siungongsi.controller.spec.UserControllerSpec;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.bob.siungongsi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users") // 사용자 관련 API의 기본 경로
public class UserController implements UserControllerSpec {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Override
  @GetMapping("/notification-status")
  public ResponseEntity<ApiResponseWrapper<?>> getNotificationStatus(
      @RequestHeader("Authorization") String authorization) {
    NotificationStatusResponse response = userService.getNotificationStatus();
    return ResponseEntity.status(ApiResponseCode.USER_GET_STATUS.getHttpStatus())
        .body(ApiResponseWrapper.success(USER_GET_STATUS, response));
  }

  @Override
  @PatchMapping("/notification-status")
  public ResponseEntity<ApiResponseWrapper<?>> updateNotificationStatus(
      @RequestHeader("Authorization") String authorization,
      @RequestBody UserNotificationRequest request) {
    NotificationStatusResponse response = userService.updateNotificationStatus(request);
    return ResponseEntity.status(ApiResponseCode.USER_UPDATE_STATUS_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(USER_UPDATE_STATUS_SUCCESS, response));
  }

  @GetMapping("/subscriptions")
  public ResponseEntity<ApiResponseWrapper<?>> getUserSubscriptions(
      @RequestHeader("Authorization") String authorization) {
    UserSubscriptionsResponse response = userService.getUserSubscriptions();
    return ResponseEntity.status(ApiResponseCode.USER_SUBSCRIPTIONS_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(USER_SUBSCRIPTIONS_SUCCESS, response));
  }
}
