package org.bob.siungongsi.api.controller;

import static org.bob.siungongsi.fixture.UserFixture.TEST_USER_ID;
import static org.bob.siungongsi.fixture.UserFixture.createDefaultNotificationStatusResponse;
import static org.bob.siungongsi.fixture.UserFixture.createEmptyUserSubscriptionsResponse;
import static org.bob.siungongsi.fixture.UserFixture.createNotificationStatusResponse;
import static org.bob.siungongsi.fixture.UserFixture.createUserNotificationRequest;
import static org.bob.siungongsi.fixture.UserFixture.createUserNotificationRequestWithFlag;
import static org.bob.siungongsi.fixture.UserFixture.createUserSubscriptionsResponseWithSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bob.siungongsi.api.config.SecurityConfigForApi;
import org.bob.siungongsi.api.controller.dto.UserRequest.UserNotificationRequest;
import org.bob.siungongsi.api.controller.dto.UserResponse.NotificationStatusResponse;
import org.bob.siungongsi.api.controller.dto.UserSubscriptionsResponse;
import org.bob.siungongsi.api.service.NotificationService;
import org.bob.siungongsi.api.service.UserService;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.repository.NotificationRepository;
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
@Import({SecurityConfigForApi.class, SecurityConfigBeansForTest.class})
@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private UserService userService;

  @MockitoBean private NotificationService notificationService;

  @MockitoBean private NotificationRepository notificationRepository;

  /** getNotificationStatus */
  @Test
  @DisplayName("알림 상태 조회 시 사용자의 알림 설정을 반환한다")
  void whenGetNotificationStatus_thenReturnsNotificationStatus() throws Exception {
    // given
    NotificationStatusResponse response = createDefaultNotificationStatusResponse();
    when(userService.getNotificationStatus()).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/users/notification-status").header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.USER_GET_STATUS.getCode()))
        .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID))
        .andExpect(jsonPath("$.data.notificationFlag").value(true));
  }

  @Test
  @DisplayName("알림 상태 조회 시 알림이 꺼진 상태를 반환한다")
  void whenGetNotificationStatusWithFlagOff_thenReturnsDisabledStatus() throws Exception {
    // given
    NotificationStatusResponse response = createNotificationStatusResponse(TEST_USER_ID, false);
    when(userService.getNotificationStatus()).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/users/notification-status").header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.USER_GET_STATUS.getCode()))
        .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID))
        .andExpect(jsonPath("$.data.notificationFlag").value(false));
  }

  /** updateNotificationStatus */
  @Test
  @DisplayName("알림 상태 업데이트 시 성공 응답을 반환한다")
  void whenUpdateNotificationStatus_thenReturnsSuccess() throws Exception {
    // given
    UserNotificationRequest request = createUserNotificationRequestWithFlag(true);
    NotificationStatusResponse response = createNotificationStatusResponse(TEST_USER_ID, true);
    when(userService.updateNotificationStatus(any(UserNotificationRequest.class)))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(
            patch("/v1/users/notification-status")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.USER_UPDATE_STATUS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID))
        .andExpect(jsonPath("$.data.notificationFlag").value(true));
  }

  @Test
  @DisplayName("알림을 끄는 상태로 업데이트 시 성공 응답을 반환한다")
  void whenUpdateNotificationStatusToOff_thenReturnsSuccess() throws Exception {
    // given
    UserNotificationRequest request = createUserNotificationRequestWithFlag(false);
    NotificationStatusResponse response = createNotificationStatusResponse(TEST_USER_ID, false);
    when(userService.updateNotificationStatus(any(UserNotificationRequest.class)))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(
            patch("/v1/users/notification-status")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.USER_UPDATE_STATUS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.notificationFlag").value(false));
  }

  @Test
  @DisplayName("pushToken과 함께 알림 상태를 업데이트한다")
  void whenUpdateNotificationStatusWithPushToken_thenReturnsSuccess() throws Exception {
    // given
    UserNotificationRequest request = createUserNotificationRequest(true, "fcm-token-123");
    NotificationStatusResponse response = createNotificationStatusResponse(TEST_USER_ID, true);
    when(userService.updateNotificationStatus(any(UserNotificationRequest.class)))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(
            patch("/v1/users/notification-status")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.USER_UPDATE_STATUS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID));
  }

  /** getUserSubscriptions */
  @Test
  @DisplayName("사용자 구독 목록 조회 시 구독한 회사 목록을 반환한다")
  void whenGetUserSubscriptions_thenReturnsSubscriptions() throws Exception {
    // given
    UserSubscriptionsResponse response = createUserSubscriptionsResponseWithSize(3);
    when(userService.getUserSubscriptions()).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/users/subscriptions").header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.USER_SUBSCRIPTIONS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID))
        .andExpect(jsonPath("$.data.subscribedCompanies").isArray())
        .andExpect(jsonPath("$.data.subscribedCompanies.length()").value(3));
  }

  @Test
  @DisplayName("구독한 회사가 없으면 빈 리스트를 반환한다")
  void whenGetUserSubscriptionsWithNoSubscriptions_thenReturnsEmptyList() throws Exception {
    // given
    UserSubscriptionsResponse response = createEmptyUserSubscriptionsResponse();
    when(userService.getUserSubscriptions()).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/users/subscriptions").header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.USER_SUBSCRIPTIONS_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID))
        .andExpect(jsonPath("$.data.subscribedCompanies").isArray())
        .andExpect(jsonPath("$.data.subscribedCompanies.length()").value(0));
  }
}
