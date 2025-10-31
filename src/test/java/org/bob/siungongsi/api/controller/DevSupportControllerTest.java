package org.bob.siungongsi.api.controller;

import static org.bob.siungongsi.fixture.DevSupportFixture.DEFAULT_EXPIRATION_TIME;
import static org.bob.siungongsi.fixture.DevSupportFixture.DEV_SUPPORT_RESPONSE_CODE;
import static org.bob.siungongsi.fixture.DevSupportFixture.DEV_SUPPORT_TOKEN_MESSAGE;
import static org.bob.siungongsi.fixture.DevSupportFixture.DEV_SUPPORT_USER_MESSAGE;
import static org.bob.siungongsi.fixture.DevSupportFixture.TEST_JWT_TOKEN;
import static org.bob.siungongsi.fixture.DevSupportFixture.TEST_JWT_TOKEN_DEFAULT;
import static org.bob.siungongsi.fixture.DevSupportFixture.TEST_JWT_TOKEN_WITH_EXPIRATION;
import static org.bob.siungongsi.fixture.UserFixture.TEST_USER_ID;
import static org.bob.siungongsi.fixture.UserFixture.mockedUser;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.bob.siungongsi.api.config.SecurityConfigForApi;
import org.bob.siungongsi.api.service.UserService;
import org.bob.siungongsi.common.domain.UserEntity;
import org.bob.siungongsi.common.repository.NotificationRepository;
import org.bob.siungongsi.common.security.JwtProvider;
import org.bob.siungongsi.testhelper.config.SecurityConfigBeansForTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles({"test", "dev"}) // DevSupportController 활성화를 위해 dev 프로필 추가
@Import({SecurityConfigForApi.class, SecurityConfigBeansForTest.class})
@WebMvcTest(DevSupportController.class)
class DevSupportControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  @MockitoBean private JwtProvider jwtProvider;

  @MockitoBean private NotificationRepository notificationRepository;

  /** getUser */
  @Test
  @DisplayName("전체 사용자 목록 조회 시 사용자 리스트를 반환한다")
  void whenGetUser_thenReturnsUserList() throws Exception {
    // given
    List<UserEntity> users =
        List.of(
            mockedUser().withId(1L).build(),
            mockedUser().withId(2L).build(),
            mockedUser().withId(3L).build());
    when(userService.getUser()).thenReturn(users);

    // when & then
    mockMvc
        .perform(get("/support/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DEV_SUPPORT_RESPONSE_CODE))
        .andExpect(jsonPath("$.message").value(DEV_SUPPORT_USER_MESSAGE))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(3));
  }

  @Test
  @DisplayName("사용자가 없으면 빈 리스트를 반환한다")
  void whenGetUserWithNoUsers_thenReturnsEmptyList() throws Exception {
    // given
    when(userService.getUser()).thenReturn(List.of());

    // when & then
    mockMvc
        .perform(get("/support/user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DEV_SUPPORT_RESPONSE_CODE))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(0));
  }

  /** getToken */
  @Test
  @DisplayName("userId로 JWT 토큰 생성 시 토큰을 반환한다")
  void whenGetToken_thenReturnsJwtToken() throws Exception {
    // given
    String userId = String.valueOf(TEST_USER_ID);
    when(jwtProvider.createJwtToken(userId)).thenReturn(TEST_JWT_TOKEN);

    // when & then
    mockMvc
        .perform(post("/support/token").param("userId", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DEV_SUPPORT_RESPONSE_CODE))
        .andExpect(jsonPath("$.message").value(DEV_SUPPORT_TOKEN_MESSAGE))
        .andExpect(jsonPath("$.data").value(TEST_JWT_TOKEN));
  }

  @Test
  @DisplayName("userId와 expirationTime으로 JWT 토큰 생성 시 토큰을 반환한다")
  void whenGetTokenWithExpirationTime_thenReturnsJwtToken() throws Exception {
    // given
    String userId = String.valueOf(TEST_USER_ID);
    when(jwtProvider.createJwtToken(eq(userId), eq(DEFAULT_EXPIRATION_TIME)))
        .thenReturn(TEST_JWT_TOKEN_WITH_EXPIRATION);

    // when & then
    mockMvc
        .perform(
            post("/support/token")
                .param("userId", userId)
                .param("expirationTime", String.valueOf(DEFAULT_EXPIRATION_TIME)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DEV_SUPPORT_RESPONSE_CODE))
        .andExpect(jsonPath("$.message").value(DEV_SUPPORT_TOKEN_MESSAGE))
        .andExpect(jsonPath("$.data").value(TEST_JWT_TOKEN_WITH_EXPIRATION));
  }

  @Test
  @DisplayName("expirationTime 없이 토큰 생성 시 기본 만료 시간으로 토큰을 생성한다")
  void whenGetTokenWithoutExpirationTime_thenReturnsDefaultToken() throws Exception {
    // given
    String userId = String.valueOf(TEST_USER_ID);
    when(jwtProvider.createJwtToken(userId)).thenReturn(TEST_JWT_TOKEN_DEFAULT);

    // when & then
    mockMvc
        .perform(post("/support/token").param("userId", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(DEV_SUPPORT_RESPONSE_CODE))
        .andExpect(jsonPath("$.data").value(TEST_JWT_TOKEN_DEFAULT));
  }
}
