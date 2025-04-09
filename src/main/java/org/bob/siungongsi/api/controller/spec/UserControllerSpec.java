package org.bob.siungongsi.api.controller.spec;

import org.bob.siungongsi.api.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API", description = "회원 알림 허용 여부 조회 및 변경 API")
public interface UserControllerSpec {

  @Operation(
      summary = "알림 허용 여부 조회",
      description = "회원의 알림 허용 여부를 반환하는 API (설정 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "알림 상태 조회 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 상태 조회 성공",
                          value =
                              "{ \"code\": 3200, \"message\": \"get_notification_status\", \"data\": { \"userId\": 1, \"notificationFlag\": true } }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "JWT 토큰이 필요함",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "JWT 토큰 필요",
                          value = "{ \"code\": 3400, \"message\": \"required_authorization\" }")
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
                          value = "{ \"code\": 3500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<?>> getNotificationStatus(
      @Parameter(
              description = "JWT 토큰 (Bearer 포함)",
              required = true,
              example = "Bearer your_token_here")
          String authorization);

  @Operation(
      summary = "알림 허용 상태 변경",
      description = "회원의 알림 허용 여부를 변경하는 API (설정 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "알림 허용 상태 변경 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 허용 상태 변경 성공",
                          value =
                              "{ \"code\": 3201, \"message\": \"update_notification_status_success\", \"data\": { \"userId\": 1, \"notificationFlag\": true } }")
                    })),
        @ApiResponse(
            responseCode = "400",
            description = "이미 같은 상태로 설정되어 있음",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "알림 상태 이미 존재",
                          value =
                              "{ \"code\": 3401, \"message\": \"notification_status_already_exist\" }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "JWT 토큰이 필요함",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "JWT 토큰 필요",
                          value = "{ \"code\": 3400, \"message\": \"required_authorization\" }")
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
                          value = "{ \"code\": 3500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<?>> updateNotificationStatus(
      @Parameter(
              description = "JWT 토큰 (Bearer 포함)",
              required = true,
              example = "Bearer your_token_here")
          String authorization,
      UserNotificationRequest request);

  @Operation(
      summary = "구독 중인 기업 목록 조회",
      description = "회원이 현재 구독 중인 기업 목록을 반환하는 API",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "구독 목록 조회 성공",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "구독 목록 조회 성공",
                          value =
                              "{ \"code\": 1000, \"message\": \"SUCCESS\", \"data\": { \"userId\": 1, \"subscribedCompanies\": [{ \"companyId\": 1, \"companyName\": \"삼성전자\", \"companyCode\": \"00593\", \"stockCode\": \"005930\" }, { \"companyId\": 2, \"companyName\": \"카카오\", \"companyCode\": \"03569\", \"stockCode\": \"035720\" }] } }")
                    })),
        @ApiResponse(
            responseCode = "401",
            description = "JWT 토큰이 필요함",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "JWT 토큰 필요",
                          value = "{ \"code\": 3400, \"message\": \"required_authorization\" }")
                    })),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponseWrapper.class),
                    examples = {
                      @ExampleObject(
                          name = "사용자 없음",
                          value = "{ \"code\": 2400, \"message\": \"user_not_found\" }")
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
                          value = "{ \"code\": 3500, \"message\": \"internal_server_error\" }")
                    }))
      })
  ResponseEntity<ApiResponseWrapper<?>> getUserSubscriptions(
      @Parameter(
              description = "JWT 토큰 (Bearer 포함)",
              required = true,
              example = "Bearer your_token_here")
          String authorization);
}
