package org.bob.siungongsi.api.controller;

import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_ID;
import static org.bob.siungongsi.fixture.NotificationFixture.createEmptyRecommendedCompanyList;
import static org.bob.siungongsi.fixture.NotificationFixture.createNotificationCompanyRequest;
import static org.bob.siungongsi.fixture.NotificationFixture.createRecommendedCompanyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bob.siungongsi.api.config.SecurityConfigForApi;
import org.bob.siungongsi.api.controller.dto.NotificationRequest.NotificationCompanyRequest;
import org.bob.siungongsi.api.controller.dto.NotificationResponse.NotificationRecommendedCompanyList;
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
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private NotificationService notificationService;

  @MockitoBean private UserService userService;

  @MockitoBean private NotificationRepository notificationRepository;

  /** getRecommendedCompanies */
  @Test
  @DisplayName("추천 회사 목록 조회 시 목록을 반환한다")
  void whenGetRecommendedCompanies_thenReturnsCompanyList() throws Exception {
    // given
    NotificationRecommendedCompanyList response = createRecommendedCompanyList(5);
    when(notificationService.recommendedCompanyNotification()).thenReturn(response);

    // when & then
    mockMvc
        .perform(
            get("/v1/notifications/recommended-companies")
                .header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code")
                .value(ApiResponseCode.NOTIFICATION_RECOMMENDED_COMPANY_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.companies").isArray())
        .andExpect(jsonPath("$.data.companies.length()").value(5));
  }

  @Test
  @DisplayName("추천 회사 목록이 비어있으면 빈 리스트를 반환한다")
  void whenGetRecommendedCompaniesWithNoResults_thenReturnsEmptyList() throws Exception {
    // given
    NotificationRecommendedCompanyList response = createEmptyRecommendedCompanyList();
    when(notificationService.recommendedCompanyNotification()).thenReturn(response);

    // when & then
    mockMvc
        .perform(
            get("/v1/notifications/recommended-companies")
                .header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code")
                .value(ApiResponseCode.NOTIFICATION_RECOMMENDED_COMPANY_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.companies").isArray())
        .andExpect(jsonPath("$.data.companies.length()").value(0));
  }

  /** addNotification */
  @Test
  @DisplayName("알림 구독 추가 시 성공 응답을 반환한다")
  void whenAddNotification_thenReturnsSuccess() throws Exception {
    // given
    NotificationCompanyRequest request = createNotificationCompanyRequest(TEST_COMPANY_ID);
    when(notificationService.createNotification(any(NotificationCompanyRequest.class)))
        .thenReturn(null);

    // when & then
    mockMvc
        .perform(
            post("/v1/notifications")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(
            jsonPath("$.code").value(ApiResponseCode.NOTIFICATION_SUBSCRIPTION_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data").doesNotExist());
  }

  /** removeNotification */
  @Test
  @DisplayName("알림 구독 삭제 시 성공 응답을 반환한다")
  void whenRemoveNotification_thenReturnsSuccess() throws Exception {
    // given
    doNothing().when(notificationService).deleteNotification(eq(TEST_COMPANY_ID));

    // when & then
    mockMvc
        .perform(
            delete("/v1/notifications/{companyId}", TEST_COMPANY_ID)
                .header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code").value(ApiResponseCode.NOTIFICATION_UNSUBSCRIBE_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data").doesNotExist());
  }

  @Test
  @DisplayName("유효하지 않은 companyId로 삭제 요청 시 400 Bad Request를 반환한다")
  void whenRemoveNotificationWithInvalidId_thenReturnsBadRequest() throws Exception {
    // when & then
    mockMvc
        .perform(
            delete("/v1/notifications/{companyId}", -1)
                .header("Authorization", "Bearer test-token"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("companyId가 0일 때 삭제 요청 시 400 Bad Request를 반환한다")
  void whenRemoveNotificationWithZeroId_thenReturnsBadRequest() throws Exception {
    // when & then
    mockMvc
        .perform(
            delete("/v1/notifications/{companyId}", 0).header("Authorization", "Bearer test-token"))
        .andExpect(status().isBadRequest());
  }
}
