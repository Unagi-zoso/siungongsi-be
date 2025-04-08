package org.bob.siungongsi.controller;

import org.bob.siungongsi.controller.dto.NotificationRequest.NotificationCompanyRequest;
import org.bob.siungongsi.controller.dto.NotificationResponse;
import org.bob.siungongsi.controller.spec.NotificationControllerSpec;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.bob.siungongsi.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/v1/notifications")
@Validated
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

    return ResponseEntity.status(
            ApiResponseCode.NOTIFICATION_RECOMMENDED_COMPANY_SUCCESS.getHttpStatus())
        .body(
            ApiResponseWrapper.success(
                ApiResponseCode.NOTIFICATION_RECOMMENDED_COMPANY_SUCCESS, companies));
  }

  @PostMapping
  public ResponseEntity<ApiResponseWrapper<?>> addNotification(
      @RequestHeader("Authorization") String authorization,
      @RequestBody NotificationCompanyRequest request) {

    notificationService.createNotification(request);

    return ResponseEntity.status(ApiResponseCode.NOTIFICATION_SUBSCRIPTION_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(ApiResponseCode.NOTIFICATION_SUBSCRIPTION_SUCCESS, null));
  }

  @DeleteMapping("/{companyId}")
  public ResponseEntity<ApiResponseWrapper<?>> removeNotification(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("companyId") @Positive Long companyId) {

    notificationService.deleteNotification(companyId);

    return ResponseEntity.status(ApiResponseCode.NOTIFICATION_UNSUBSCRIBE_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(ApiResponseCode.NOTIFICATION_UNSUBSCRIBE_SUCCESS, null));
  }
}
