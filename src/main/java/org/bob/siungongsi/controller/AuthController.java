package org.bob.siungongsi.controller;

import java.util.List;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.controller.dto.AuthResponse.LoginSuccessResponse;
import org.bob.siungongsi.controller.dto.TermsResponse;
import org.bob.siungongsi.controller.spec.AuthControllerSpec;
import org.bob.siungongsi.domain.UserEntity;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.dto.ApiResponseWrapper;
import org.bob.siungongsi.service.AuthService;
import org.bob.siungongsi.service.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth") // 회원 관련 API의 기본 경로
public class AuthController implements AuthControllerSpec {
  private final AuthService authService;
  private final KakaoAuthService kakaoAuthService;

  public AuthController(AuthService authService, KakaoAuthService kakaoAuthService) {
    this.authService = authService;
    this.kakaoAuthService = kakaoAuthService;
  }

  @Override
  @PostMapping("/register")
  public ResponseEntity<ApiResponseWrapper<?>> registerUser(
      @RequestBody AuthRequest.RegisterRequest authRequest) {

    String socialId = authRequest.socialId();
    String accessToken = authRequest.accessToken();

    if (socialId == null || socialId.isBlank() || socialId.length() > 100) {
      return ResponseEntity.status(401)
          .body(ApiResponseWrapper.error(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION));
    }

    UserEntity user = authService.authRequest(authRequest);

    return ResponseEntity.status(201)
        .body(ApiResponseWrapper.success(ApiResponseCode.AUTH_REGISTER_SUCCESS));
  }

  @Override
  @PostMapping("/login")
  public ResponseEntity<ApiResponseWrapper<LoginSuccessResponse>> loginUser(
      @RequestBody AuthRequest.LoginRequest authRequest) {

    String accessToken = authRequest.accessToken();
    if (accessToken == null || accessToken.isBlank()) {
      return ResponseEntity.status(401)
          .body(ApiResponseWrapper.error(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION));
    }

    String socialId = kakaoAuthService.validateAccessToken(accessToken);
    if (socialId == null) {
      return ResponseEntity.status(401)
          .body(ApiResponseWrapper.error(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION));
    }

    UserEntity user = authService.login(authRequest, socialId);

    return ResponseEntity.ok(
        ApiResponseWrapper.success(
            ApiResponseCode.AUTH_LOGIN_SUCCESS, LoginSuccessResponse.of(accessToken)));
  }

  @Override
  @GetMapping("/terms")
  public ResponseEntity<ApiResponseWrapper<?>> getTerms() {
    return ResponseEntity.ok(
        ApiResponseWrapper.success(
            ApiResponseCode.AUTH_GET_TERMS_SUCCESS,
            List.of(
                TermsResponse.of(1, "이용약관 (필수)", "약관 내용"),
                TermsResponse.of(2, "개인정보 수집 및 이용 (필수)", "약관 내용"))));
  }

  @Override
  @DeleteMapping("/withdraw")
  public ResponseEntity<ApiResponseWrapper<?>> withdrawUser(
      @RequestHeader("Authorization") String authorization) {

    if (authorization == null || authorization.isBlank() || !authorization.startsWith("Bearer ")) {
      return ResponseEntity.status(401)
          .body(ApiResponseWrapper.error(ApiResponseCode.AUTH_REQUIRED_AUTHORIZATION));
    }

    String accessToken = authorization.substring(7); // Remove "Bearer " prefix

    authService.withdrawUser(accessToken);

    return ResponseEntity.ok(
        ApiResponseWrapper.success(ApiResponseCode.AUTH_WITHDRAW_USER_SUCCESS, null));
  }
}
