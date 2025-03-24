package org.bob.siungongsi.controller.spec;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.controller.dto.AuthResponse.LoginSuccessResponse;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 인증 API", description = "회원 로그인, 탈퇴 및 약관 조회 API") // Swagger에서 API 그룹 지정
public interface AuthControllerSpec {

  @Operation(
      summary = "로그인",
      description = "회원 인증 후 JWT 토큰을 반환하는 API (로그인 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "로그인 성공",
                        value =
                            "{ \"code\": 2202, \"message\": \"login_success\", \"data\": { \"accessToken\": \"your_token_here\" } }")
                  })
            }),
        @ApiResponse(
            responseCode = "401",
            description = "액세스 토큰 만료 또는 인증 필요",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "인증 실패",
                        value = "{ \"code\": 2400, \"message\": \"required_authorization\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "서버 오류",
                        value = "{ \"code\": 2500, \"message\": \"internal_server_error\" }")
                  })
            })
      })
  ResponseEntity<ApiResponseWrapper<LoginSuccessResponse>> loginUser(
      @Parameter(
              description = "JWT 토큰 (Bearer 포함)",
              required = true,
              example = "Bearer your_token_here")
          String authorization);

  @Operation(
      summary = "회원가입",
      description = "회원 정보를 등록하는 API (회원가입 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "회원가입 성공",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "회원가입 성공",
                        value = "{ \"code\": 2203, \"message\": \"register_success\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "401",
            description = "액세스 토큰 만료",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "액세스 토큰 만료",
                        value = "{ \"code\": 2401, \"message\": \"access_token_expired\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "401",
            description = "JWT 인증 필요",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "JWT 인증 필요",
                        value = "{ \"code\": 2400, \"message\": \"required_authorization\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "서버 오류",
                        value = "{ \"code\": 2500, \"message\": \"internal_server_error\" }")
                  })
            })
      })
  ResponseEntity<ApiResponseWrapper<?>> registerUser(
      AuthRequest.RegisterRequest requestDto,
      @Parameter(
              description = "JWT 토큰 (Bearer 포함)",
              required = true,
              example = "Bearer your_token_here")
          String authorization);

  @Operation(
      summary = "약관 조회",
      description = "회원가입 시 필요한 약관 정보를 가져오는 API (회원가입 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "약관 조회 성공",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "약관 조회 성공",
                        value =
                            "{ \"code\": 2201, \"message\": \"get_terms_success\", \"data\": [ { \"id\": 1, \"title\": \"이용약관 (필수)\", \"content\": \"약관 내용\" }, { \"id\": 2, \"title\": \"개인정보 수집 및 이용 (필수)\", \"content\": \"약관 내용\" } ] }")
                  })
            }),
        @ApiResponse(
            responseCode = "404",
            description = "약관을 찾을 수 없음",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "약관 없음",
                        value = "{ \"code\": 2403, \"message\": \"terms_not_found\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "서버 오류",
                        value = "{ \"code\": 2500, \"message\": \"internal_server_error\" }")
                  })
            })
      })
  ResponseEntity<ApiResponseWrapper<?>> getTerms();

  /** 회원 탈퇴 API */
  @DeleteMapping("/withdraw")
  @Operation(
      summary = "회원 탈퇴",
      description = "회원 정보를 삭제하는 API (설정 페이지에서 사용)",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "회원 탈퇴 성공",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "회원 탈퇴 성공",
                        value = "{ \"code\": 2200, \"message\": \"withdraw_user_success\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "401",
            description = "JWT 토큰이 필요함",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "JWT 토큰이 필요함",
                        value = "{ \"code\": 2400, \"message\": \"required_authorization\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "사용자 없음",
                        value = "{ \"code\": 2402, \"message\": \"user_not_found\" }")
                  })
            }),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ApiResponseWrapper.class),
                  examples = {
                    @ExampleObject(
                        name = "서버 오류",
                        value = "{ \"code\": 2500, \"message\": \"internal_server_error\" }")
                  })
            })
      })
  ResponseEntity<ApiResponseWrapper<?>> withdrawUser(
      @Parameter(
              description = "JWT 토큰 (Bearer 포함)",
              required = true,
              example = "Bearer your_token_here")
          String authorization);
}
