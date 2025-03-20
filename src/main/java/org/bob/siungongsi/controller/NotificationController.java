package org.bob.siungongsi.controller;

import org.bob.siungongsi.controller.dto.NotificationRequest.NotificationCompanyRequest;
import org.bob.siungongsi.controller.dto.NotificationResponse;
import org.bob.siungongsi.controller.spec.NotificationControllerSpec;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.bob.siungongsi.exception.CustomException;
import org.bob.siungongsi.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/notifications")
public class NotificationController implements NotificationControllerSpec {
  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("/recommended-companies")
  public ResponseEntity<ApiResponseWrapper<?>> getRecommendedCompanies(
      @RequestHeader("Authorization") String authorization) {
    NotificationResponse.NotificationRecommendedCompanyList companies =
        notificationService.recommendedCompanyNotification();

    return ResponseEntity.ok(
        ApiResponseWrapper.success(
            ApiResponseCode.NOTIFICATION_RECOMMENDED_COMPANY_SUCCESS, companies));
  }

  @PostMapping
  public ResponseEntity<ApiResponseWrapper<?>> addNotification(
      @RequestHeader("Authorization") String authorization,
      @RequestBody NotificationCompanyRequest request) {

    try {
      notificationService.createNotification(request);
      return ResponseEntity.ok(
          ApiResponseWrapper.success(ApiResponseCode.NOTIFICATION_SUBSCRIPTION_SUCCESS, null));
    } catch (CustomException e) {
      HttpStatus status;

      if (e.getErrorCode() == ApiResponseCode.NOTIFICATION_ALREADY_EXISTS) {
        status = HttpStatus.CONFLICT; // 409
      } else if (e.getErrorCode() == ApiResponseCode.NOTIFICATION_INVALID_COMPANY_ID) {
        status = HttpStatus.NOT_FOUND; // 404
      } else if (e.getErrorCode() == ApiResponseCode.NOTIFICATION_REQUIRED_STATUS) {
        status = HttpStatus.FORBIDDEN; // 403
      } else {
        status = HttpStatus.BAD_REQUEST; // 기본값
      }

      return ResponseEntity.status(status).body(ApiResponseWrapper.error(e.getErrorCode()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponseWrapper.error(ApiResponseCode.NOTIFICATION_INTERNAL_SERVER_ERROR));
    }
  }

  @DeleteMapping("/{companyId}")
  public ResponseEntity<ApiResponseWrapper<?>> removeNotification(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("companyId") Long companyId) {

    notificationService.deleteNotification(companyId);
    return ResponseEntity.ok(
        ApiResponseWrapper.success(ApiResponseCode.NOTIFICATION_UNSUBSCRIBE_SUCCESS, null));
  }
}
