package org.bob.siungongsi.controller;

import static org.bob.siungongsi.dto.ApiResponseCode.GONGSI_DETAIL_SUCCESS;
import static org.bob.siungongsi.dto.ApiResponseCode.GONGSI_LIST_SUCCESS;

import org.bob.siungongsi.controller.dto.CompanyResponse;
import org.bob.siungongsi.controller.dto.GongsiResponse.GongsiDetailResponse;
import org.bob.siungongsi.controller.dto.GongsiResponse.GongsiListResponse;
import org.bob.siungongsi.controller.spec.GongsiControllerSpec;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.bob.siungongsi.repository.NotificationRepository;
import org.bob.siungongsi.service.GongsiService;
import org.bob.siungongsi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/gongsi") // 공시 API의 기본 경로
public class GongsiController implements GongsiControllerSpec {

  private final GongsiService gongsiService;
  private final UserService userService;
  private final NotificationRepository notificationRepository;

  public GongsiController(
      GongsiService gongsiService,
      UserService userService,
      NotificationRepository notificationRepository) {
    this.gongsiService = gongsiService;
    this.userService = userService;
    this.notificationRepository = notificationRepository;
  }

  @Override
  @GetMapping
  public ResponseEntity<ApiResponseWrapper<GongsiListResponse>> getGongsiList(
      @RequestParam(required = false) Long companyId,
      @RequestParam(defaultValue = "latest") String sort,
      @RequestParam(defaultValue = "false") Boolean content,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "8") Integer size,
      @RequestParam(required = false) String startDate,
      @RequestParam(required = false) String endDate) {

    GongsiListResponse response =
        gongsiService.getGongsiList(companyId, sort, content, page, size, startDate, endDate);

    return ResponseEntity.status(ApiResponseCode.GONGSI_LIST_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(GONGSI_LIST_SUCCESS, response));
  }

  @Override
  @GetMapping("/{gongsiId}")
  public ResponseEntity<ApiResponseWrapper<GongsiDetailResponse>> getGongsiDetail(
      @Parameter(description = "공시 ID", example = "101", required = true) @PathVariable("gongsiId")
          String gongsiId,
      HttpServletRequest request) {

    String ipAddress = getClientIpAddress(request);

    boolean isSubscribed = false;

    try {
      Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      if (userId != null) {
        Long companyId = gongsiService.getCompanyIdByGongsiId(gongsiId);

        isSubscribed = notificationRepository.existsByUserIdAndCompanyId(userId, companyId);
      }
    } catch (Exception e) {
    }

    GongsiDetailResponse response = gongsiService.getGongsiDetail(gongsiId, ipAddress);

    if (response != null) {
      CompanyResponse.CompanyInfo companyInfo = response.company();
      CompanyResponse.CompanyInfo updatedCompanyInfo =
          CompanyResponse.CompanyInfo.of(
              companyInfo.id(), companyInfo.name(), companyInfo.prdyCtr(), isSubscribed);

      response = GongsiDetailResponse.of(response.gongsi(), updatedCompanyInfo);
    }

    return ResponseEntity.status(ApiResponseCode.GONGSI_DETAIL_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(GONGSI_DETAIL_SUCCESS, response));
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-Forwarded-For");
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
    }
    return ipAddress;
  }
}
