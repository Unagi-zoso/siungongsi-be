package org.bob.siungongsi.fixture;

import java.util.List;

import org.bob.siungongsi.api.controller.dto.AuthRequest;
import org.bob.siungongsi.api.controller.dto.AuthResponse;

public class AuthFixture {

  // 테스트용 소셜 ID 상수
  public static final String TEST_SOCIAL_ID = "12345678";
  public static final String TEST_SOCIAL_ID_2 = TEST_SOCIAL_ID + "1";

  // 테스트용 토큰 상수
  public static final String TEST_KAKAO_ACCESS_TOKEN = "test-kakao-access-token";
  public static final String TEST_KAKAO_ACCESS_TOKEN_2 = TEST_KAKAO_ACCESS_TOKEN + "-2";
  public static final String TEST_BEARER_TOKEN = "Bearer " + TEST_KAKAO_ACCESS_TOKEN;
  public static final String TEST_BEARER_TOKEN_2 = "Bearer " + TEST_KAKAO_ACCESS_TOKEN_2;
  public static final String TEST_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test-jwt-token";
  public static final String TEST_JWT_TOKEN_2 = TEST_JWT_TOKEN + "-2";
  public static final String INVALID_JWT_TOKEN = "invalid.jwt.token";

  // 약관 ID 상수
  public static final Long REQUIRED_TERM_ID_1 = 1L;
  public static final Long REQUIRED_TERM_ID_2 = 2L;
  public static final Long OPTIONAL_TERM_ID = 3L;
  public static final List<Long> ALL_REQUIRED_TERM_IDS =
      List.of(REQUIRED_TERM_ID_1, REQUIRED_TERM_ID_2);
  public static final List<Long> ALL_TERM_IDS =
      List.of(REQUIRED_TERM_ID_1, REQUIRED_TERM_ID_2, OPTIONAL_TERM_ID);

  public static class RegisterRequestBuilder {
    private List<Long> agreedTermIds = ALL_TERM_IDS;

    public RegisterRequestBuilder agreedTermIds(List<Long> agreedTermIds) {
      this.agreedTermIds = agreedTermIds;
      return this;
    }

    public AuthRequest.RegisterRequest build() {
      return new AuthRequest.RegisterRequest(agreedTermIds);
    }
  }

  public static RegisterRequestBuilder registerRequest() {
    return new RegisterRequestBuilder();
  }

  public static class LoginSuccessResponseBuilder {
    private String accessToken = TEST_JWT_TOKEN;
    private boolean isUser = true;

    public LoginSuccessResponseBuilder accessToken(String accessToken) {
      this.accessToken = accessToken;
      return this;
    }

    public LoginSuccessResponseBuilder isUser(boolean isUser) {
      this.isUser = isUser;
      return this;
    }

    public LoginSuccessResponseBuilder newUser() {
      this.accessToken = null;
      this.isUser = false;
      return this;
    }

    public AuthResponse.LoginSuccessResponse build() {
      return AuthResponse.LoginSuccessResponse.of(accessToken, isUser);
    }
  }

  public static LoginSuccessResponseBuilder loginSuccessResponse() {
    return new LoginSuccessResponseBuilder();
  }

  // 편의 메서드
  public static AuthRequest.RegisterRequest createValidRegisterRequest() {
    return registerRequest().build();
  }

  public static AuthRequest.RegisterRequest createRegisterRequestWithOnlyRequired() {
    return registerRequest().agreedTermIds(ALL_REQUIRED_TERM_IDS).build();
  }

  public static AuthResponse.LoginSuccessResponse createExistingUserLoginResponse() {
    return loginSuccessResponse().build();
  }

  public static AuthResponse.LoginSuccessResponse createNewUserLoginResponse() {
    return loginSuccessResponse().newUser().build();
  }
}
