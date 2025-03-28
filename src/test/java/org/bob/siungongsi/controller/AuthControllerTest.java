package org.bob.siungongsi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.bob.siungongsi.controller.dto.AuthRequest;
import org.bob.siungongsi.controller.dto.AuthResponse;
import org.bob.siungongsi.dto.ApiResponseCode;
import org.bob.siungongsi.service.AuthService;
import org.bob.siungongsi.service.KakaoAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper om;

  @MockitoBean private AuthService authService;

  @MockitoBean private KakaoAuthService kakaoAuthService;

  private final String VALID_ACCESS_TOKEN = "VALIDTOKEN";
  private final String INVALID_ACCESS_TOKEN = "INVALIDTOKEN";

  @Test
  @DisplayName("정상적인 회원가입 요청 시 유저 등록 후 201를 응답해야 한다")
  public void givenValidRequest_whenRegister_thenReturn201() throws Exception {
    AuthRequest.RegisterRequest authRequest = new AuthRequest.RegisterRequest(List.of(1L, 2L));

    String accessToken = "Bearer testAccessToken";
    String expectedJwt = "generatedJwtToken";

    when(authService.register(any(AuthRequest.RegisterRequest.class), any(String.class)))
        .thenReturn(expectedJwt);

    mockMvc
        .perform(
            post("/v1/auth/register")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(authRequest)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.AUTH_REGISTER_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data").value(expectedJwt));

    verify(authService).register(any(AuthRequest.RegisterRequest.class), any(String.class));
  }

  @Test
  @DisplayName("정상적인 로그인 요청 시 jwt토큰과 함께 200을 반환해야 한다")
  void givenValidAccessToken_whenLogin_thenReturn200() throws Exception {
    AuthResponse.LoginSuccessResponse mockResponse =
        new AuthResponse.LoginSuccessResponse("JWT_TOKEN", true);
    when(authService.login(any(String.class))).thenReturn(mockResponse);

    mockMvc
        .perform(
            post("/v1/auth/login")
                .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN) // "Bearer " 제거 확인
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(mockResponse)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code").value(ApiResponseCode.AUTH_LOGIN_SUCCESS.getCode())) // 응답 코드 확인
        .andExpect(jsonPath("$.data.accessToken").value("JWT_TOKEN"))
        .andExpect(jsonPath("$.data.isUser").value(true));
  }
}
