package org.bob.siungongsi.api.controller.spec;

import org.bob.siungongsi.api.controller.dto.NotificationRequest.NotificationCompanyRequest;
import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;

@Tag(name = "알림 API", description = "알림 관련 API")
public interface NotificationControllerSpec {

  @Operation(
      summary = "추천 알림 기업",
      description = "이 API는 구독자수가 많은 상위 5개의 기업을 사용자에게 추천해주는 API입니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "추천 기업 목록 조회 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "추천 기업 목록 조회 성공",
                          value =
                              "{ \"code\": 5200, \"message\": \"get_recommended_company_success\", \"data\": [ { \"companyId\": 1, \"companyName\": \"삼성전자\", \"subscriberCnt\": 1500, \"isSubscribed\": true }, { \"companyId\": 2, \"companyName\": \"네이버\", \"subscriberCnt\": 1200, \"isSubscribed\": false }, { \"companyId\": 3, \"companyName\": \"카카오\", \"subscriberCnt\": 1100, \"isSubscribed\": false }, { \"companyId\": 4, \"companyName\": \"LG전자\", \"subscriberCnt\": 900, \"isSubscribed\": false }, { \"companyId\": 5, \"companyName\": \"현대자동차\", \"subscriberCnt\": 800, \"isSubscribed\": false } ] }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - Authorization 헤더가 없거나 잘못된 경우",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "인증 실패",
                          value = "{ \"code\": 5400, \"message\": \"required_authorization\" }")
                    })),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "서버 오류",
                          value = "{ \"code\": 5500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<?>> getRecommendedCompanies(String authorization);

  @Operation(
      summary = "알림 추가",
      description = "해당 API는 사용자가 특정 회사의 공시 알림을 받을 수 있도록 설정합니다.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "알림 설정 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 설정 성공",
                          value =
                              "{ \"code\": 5201, \"message\": \"notification_subscription_success\" }")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 companyId",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "잘못된 회사 ID",
                          value = "{ \"code\": 5401, \"message\": \"invalid_company_id\" }")
                    })),
        @ApiResponse(
            responseCode = "409",
            description = "알림 상태 확인 필요",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 상태 필요",
                          value =
                              "{ \"code\": 5403, \"message\": \"required_notification_status\" }")
                    })),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "서버 오류",
                          value = "{ \"code\": 5500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<?>> addNotification(
      String authorization, NotificationCompanyRequest request);

  @Operation(
      summary = "알림 제거",
      description = "특정 기업의 알림을 해제하는 API입니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "알림 해제 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 해제 성공",
                          value =
                              "{ \"code\": 5202, \"message\": \"unsubscribe_notification_success\" }")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 companyId",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "잘못된 회사 ID",
                          value = "{ \"code\": 5401, \"message\": \"invalid_company_id\" }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패 - Authorization 헤더 필요",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "인증 실패",
                          value = "{ \"code\": 5400, \"message\": \"required_authorization\" }")
                    })),
        @ApiResponse(
            responseCode = "404",
            description = "알림이 존재하지 않음",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 없음",
                          value = "{ \"code\": 5404, \"message\": \"notification_not_found\" }")
                    })),
        @ApiResponse(
            responseCode = "409",
            description = "알림 상태 확인 필요",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 상태 필요",
                          value =
                              "{ \"code\": 5403, \"message\": \"required_notification_status\" }")
                    })),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "서버 오류",
                          value = "{ \"code\": 5500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<?>> removeNotification(
      String authorization,
      @Parameter(description = "알림을 해제할 회사 ID", required = true) @Positive Long companyId);
}
