package org.bob.siungongsi.controller;

import static org.bob.siungongsi.dto.ApiResponseCode.USER_GET_STATUS;
import static org.bob.siungongsi.dto.ApiResponseCode.USER_UPDATE_STATUS_SUCCESS;

import org.bob.siungongsi.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.controller.dto.UserResponse.NotificationStatusResponse;
import org.bob.siungongsi.controller.spec.UserControllerSpec;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.bob.siungongsi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/users") // 사용자 관련 API의 기본 경로
@Tag(name = "사용자 API", description = "회원 알림 허용 여부 조회 및 변경 API") // Swagger에서 API 그룹 지정
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
    return ResponseEntity.ok(ApiResponseWrapper.success(USER_GET_STATUS, response));
  }

  @Override
  @PatchMapping("/notification-status")
  public ResponseEntity<ApiResponseWrapper<?>> updateNotificationStatus(
      @RequestHeader("Authorization") String authorization,
      @RequestBody UserNotificationRequest request) {
    NotificationStatusResponse response = userService.updateNotificationStatus(request);
    return ResponseEntity.status(201)
        .body(ApiResponseWrapper.success(USER_UPDATE_STATUS_SUCCESS, response));
  }
}
