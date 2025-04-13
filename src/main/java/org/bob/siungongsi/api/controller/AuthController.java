package org.bob.siungongsi.api.controller;

import java.util.List;

import org.bob.siungongsi.api.controller.dto.AuthRequest;
import org.bob.siungongsi.api.controller.dto.AuthResponse.LoginSuccessResponse;
import org.bob.siungongsi.api.controller.dto.TermsResponse;
import org.bob.siungongsi.api.controller.spec.AuthControllerSpec;
import org.bob.siungongsi.api.service.AuthService;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.dto.ApiResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth") // 회원 관련 API의 기본 경로
public class AuthController implements AuthControllerSpec {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Override
  @PostMapping("/register")
  public ResponseEntity<ApiResponseWrapper<?>> registerUser(
      @RequestBody AuthRequest.RegisterRequest authRequest,
      @RequestHeader("Authorization") String accessToken) {

    String jwt = authService.register(authRequest, accessToken);

    return ResponseEntity.status(ApiResponseCode.AUTH_REGISTER_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(ApiResponseCode.AUTH_REGISTER_SUCCESS, jwt));
  }

  @Override
  @PostMapping("/login")
  public ResponseEntity<ApiResponseWrapper<LoginSuccessResponse>> loginUser(
      @RequestHeader("Authorization") String accessToken) {

    LoginSuccessResponse response = authService.login(accessToken);

    return ResponseEntity.status(ApiResponseCode.AUTH_LOGIN_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(ApiResponseCode.AUTH_LOGIN_SUCCESS, response));
  }

  @Override
  @GetMapping("/terms")
  public ResponseEntity<ApiResponseWrapper<?>> getTerms() {
    List<TermsResponse> terms = authService.getTerms();
    return ResponseEntity.status(ApiResponseCode.AUTH_GET_TERMS_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(ApiResponseCode.AUTH_GET_TERMS_SUCCESS, terms));
  }

  @Override
  @DeleteMapping("/withdraw")
  public ResponseEntity<ApiResponseWrapper<?>> withdrawUser(
      @RequestHeader("Authorization") String accessToken) {

    authService.withdrawUser();

    return ResponseEntity.status(ApiResponseCode.AUTH_WITHDRAW_USER_SUCCESS.getHttpStatus())
        .body(ApiResponseWrapper.success(ApiResponseCode.AUTH_WITHDRAW_USER_SUCCESS, null));
  }
}
