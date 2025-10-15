package org.bob.siungongsi.api.controller;

import static org.bob.siungongsi.fixture.AuthFixture.INVALID_JWT_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_BEARER_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_JWT_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.createExistingUserLoginResponse;
import static org.bob.siungongsi.fixture.AuthFixture.createNewUserLoginResponse;
import static org.bob.siungongsi.fixture.AuthFixture.createValidRegisterRequest;
import static org.bob.siungongsi.fixture.TermFixture.createAllTermsResponses;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.bob.siungongsi.api.config.SecurityConfigForApi;
import org.bob.siungongsi.api.controller.dto.AuthRequest;
import org.bob.siungongsi.api.controller.dto.AuthResponse;
import org.bob.siungongsi.api.controller.dto.TermsResponse;
import org.bob.siungongsi.api.service.AuthService;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.exception.GlobalExceptionHandler;
import org.bob.siungongsi.testhelper.config.SecurityConfigBeansForTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@Import({SecurityConfigForApi.class, SecurityConfigBeansForTest.class, GlobalExceptionHandler.class})
@WebMvcTest(AuthController.class)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthService authService;

  /** registerUser */
  @Test
  @DisplayName("회원가입 시 JWT 토큰을 반환한다")
  void whenRegister_thenReturnsJwtToken() throws Exception {
    // given
    AuthRequest.RegisterRequest request = createValidRegisterRequest();
    when(authService.register(any(AuthRequest.RegisterRequest.class), eq(TEST_BEARER_TOKEN)))
        .thenReturn(TEST_JWT_TOKEN);

    // when & then
    mockMvc
        .perform(
            post("/v1/auth/register")
                .header("Authorization", TEST_BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_REGISTER_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data").value(TEST_JWT_TOKEN));
  }

  @Test
  @DisplayName("이미 존재하는 사용자로 회원가입 시 409 Conflict를 반환한다")
  void whenRegisterExistingUser_thenReturnsConflict() throws Exception {
    // given
    AuthRequest.RegisterRequest request = createValidRegisterRequest();
    when(authService.register(any(AuthRequest.RegisterRequest.class), eq(TEST_BEARER_TOKEN)))
        .thenThrow(new CustomException(ApiResponseCode.AUTH_USER_ALREADY_EXISTS));

    // when & then
    mockMvc
        .perform(
            post("/v1/auth/register")
                .header("Authorization", TEST_BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_USER_ALREADY_EXISTS.getCode()));
  }

  @Test
  @DisplayName("필수 약관 미동의 시 403 Forbidden을 반환한다")
  void whenRegisterWithoutRequiredTerms_thenReturnsForbidden() throws Exception {
    // given
    AuthRequest.RegisterRequest request = createValidRegisterRequest();
    when(authService.register(any(AuthRequest.RegisterRequest.class), eq(TEST_BEARER_TOKEN)))
        .thenThrow(new CustomException(ApiResponseCode.AUTH_REQUIRED_TERMS_NOT_AGREED));

    // when & then
    mockMvc
        .perform(
            post("/v1/auth/register")
                .header("Authorization", TEST_BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.code").value(ApiResponseCode.AUTH_REQUIRED_TERMS_NOT_AGREED.getCode()));
  }

  /** loginUser */
  @Test
  @DisplayName("기존 사용자 로그인 시 JWT 토큰을 반환한다")
  void whenLoginExistingUser_thenReturnsJwtToken() throws Exception {
    // given
    AuthResponse.LoginSuccessResponse loginResponse = createExistingUserLoginResponse();
    when(authService.login(TEST_BEARER_TOKEN)).thenReturn(loginResponse);

    // when & then
    mockMvc
        .perform(post("/v1/auth/login").header("Authorization", TEST_BEARER_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_LOGIN_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.accessToken").value(TEST_JWT_TOKEN))
        .andExpect(jsonPath("$.data.isUser").value(true));
  }

  @Test
  @DisplayName("신규 사용자 로그인 시 isUser가 false를 반환한다")
  void whenLoginNewUser_thenReturnsIsUserFalse() throws Exception {
    // given
    AuthResponse.LoginSuccessResponse loginResponse = createNewUserLoginResponse();
    when(authService.login(TEST_BEARER_TOKEN)).thenReturn(loginResponse);

    // when & then
    mockMvc
        .perform(post("/v1/auth/login").header("Authorization", TEST_BEARER_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_LOGIN_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.accessToken").isEmpty())
        .andExpect(jsonPath("$.data.isUser").value(false));

    verify(authService).login(TEST_BEARER_TOKEN);
  }

  /** getTerms */
  @Test
  @DisplayName("약관 조회 시 약관 목록을 반환한다")
  void whenGetTerms_thenReturnsTermsList() throws Exception {
    // given
    List<TermsResponse> termsResponses = createAllTermsResponses();
    when(authService.getTerms()).thenReturn(termsResponses);

    // when & then
    mockMvc
        .perform(get("/v1/auth/terms"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_GET_TERMS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(termsResponses.size()));
  }

  @Test
  @DisplayName("약관이 없을 때 404 Not Found를 반환한다")
  void whenGetTermsWithNoTerms_thenReturnsNotFound() throws Exception {
    // given
    when(authService.getTerms())
        .thenThrow(new CustomException(ApiResponseCode.AUTH_TERMS_NOT_FOUND));

    // when & then
    mockMvc
        .perform(get("/v1/auth/terms"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_TERMS_NOT_FOUND.getCode()));
  }

  /** logoutUser */
  @Test
  @DisplayName("로그아웃 시 200 OK를 반환한다")
  void whenLogout_thenReturnsOk() throws Exception {
    // given
    String bearerToken = "Bearer " + TEST_JWT_TOKEN;

    // when & then
    mockMvc
        .perform(post("/v1/auth/logout").header("Authorization", bearerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_LOGOUT_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("유효하지 않은 토큰으로 로그아웃 시 401 Unauthorized를 반환한다")
  void whenLogoutWithInvalidToken_thenReturnsUnauthorized() throws Exception {
    // given
    String invalidBearerToken = "Bearer " + INVALID_JWT_TOKEN;

    // when & then
    mockMvc
        .perform(post("/v1/auth/logout").header("Authorization", invalidBearerToken))
        .andExpect(status().isUnauthorized())
        .andExpect(
            jsonPath("$.code")
                .value(ApiResponseCode.AUTH_ACCESS_TOKEN_INVALID_SIGNATURE.getCode()));
  }

  /** withdrawUser */
  @Test
  @DisplayName("회원탈퇴 시 200 OK를 반환한다")
  void whenWithdraw_thenReturnsOk() throws Exception {
    // given
    String bearerToken = TEST_BEARER_TOKEN;

    // when & then
    mockMvc
        .perform(delete("/v1/auth/withdraw").header("Authorization", bearerToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_WITHDRAW_USER_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("유효하지 않은 토큰으로 회원탈퇴 시 401 Unauthorized를 반환한다")
  void whenWithdrawWithInvalidToken_thenReturnsUnauthorized() throws Exception {
    // given
    String invalidBearerToken = "Bearer " + INVALID_JWT_TOKEN;

    // when & then
    mockMvc
        .perform(delete("/v1/auth/withdraw").header("Authorization", invalidBearerToken))
        .andExpect(status().isUnauthorized())
        .andExpect(
            jsonPath("$.code")
                .value(ApiResponseCode.AUTH_ACCESS_TOKEN_INVALID_SIGNATURE.getCode()));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 회원탈퇴 시 404 Not Found를 반환한다")
  void whenWithdrawNonExistentUser_thenReturnsNotFound() throws Exception {
    // given
    String bearerToken = TEST_BEARER_TOKEN;
    doThrow(new CustomException(ApiResponseCode.AUTH_USER_NOT_FOUND))
        .when(authService)
        .withdrawUser();

    // when & then
    mockMvc
        .perform(delete("/v1/auth/withdraw").header("Authorization", bearerToken))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_USER_NOT_FOUND.getCode()));
  }
}
