package org.bob.siungongsi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.bob.siungongsi.controller.dto.AuthRequest;
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

  @Test
  @DisplayName("회원가입 성공")
  public void register() throws Exception {
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
}
