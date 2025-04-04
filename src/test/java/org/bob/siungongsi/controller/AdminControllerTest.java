package org.bob.siungongsi.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.bob.siungongsi.api.service.UserService;
import org.bob.siungongsi.batch.controller.AdminController;
import org.bob.siungongsi.batch.service.CompanyNameAutofillGenerator;
import org.bob.siungongsi.batch.service.ProcessingFailedGongsiService;
import org.bob.siungongsi.batch.service.TodayProcessedGongsiService;
import org.bob.siungongsi.common.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles({"test", "batch"})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminController.class)
@TestPropertySource(properties = "admin.key=test-api-key")
class AdminControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper om;

  @MockitoBean private TodayProcessedGongsiService todayProcessedGongsiService;

  @MockitoBean private ProcessingFailedGongsiService processingFailedGongsiService;

  @MockitoBean private CompanyNameAutofillGenerator companyNameAutofillGenerator;

  @MockitoBean private UserService userService;

  @MockitoBean private JwtProvider jwtProvider;

  private static final String REQUEST_URL = "/admin";

  @Test
  @DisplayName("유효한 API 키로 처리된 공시 목록 삭제 요청 시 200 OK를 반환한다")
  void givenValidApiKey_whenRemoveProcessedGongsiList_thenReturnsOk() throws Exception {
    List<String> gongsiCodes = List.of("000000", "000001");
    String requestJson = om.writeValueAsString(gongsiCodes);

    doNothing().when(todayProcessedGongsiService).removeGongsiList(List.of("CODE1", "CODE2"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(REQUEST_URL + "/remove-processed-gongsi")
                .param("api-key", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk());

    verify(todayProcessedGongsiService).removeGongsiList(gongsiCodes);
  }

  @Test
  @DisplayName("유효한 API 키로 실패한 공시 재처리 요청 시 200 OK를 반환한다")
  void givenValidApiKey_whenRetryFailedGongsiList_thenReturnsOk() throws Exception {
    List<String> gongsiCodes = List.of("000000", "000001");
    String requestJson = om.writeValueAsString(gongsiCodes);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(REQUEST_URL + "/retry-failed-gongsi")
                .param("api-key", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk());

    verify(processingFailedGongsiService).retryGongsiMessageList(gongsiCodes);
  }

  @Test
  @DisplayName("잘못된 API 키로 처리된 공시 목록 삭제 요청 시 예외를 반환한다")
  void givenInvalidApiKey_whenRemoveProcessedGongsiList_thenThrowsException() throws Exception {
    List<String> gongsiCodes = List.of("000000", "000001");
    String requestJson = om.writeValueAsString(gongsiCodes);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(REQUEST_URL + "/remove-processed-gongsi")
                .param("api-key", "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result -> result.getResolvedException().getMessage().contains("Invalid admin key"));

    verifyNoInteractions(todayProcessedGongsiService);
  }

  @DisplayName("잘못된 API 키로 실패한 공시 재처리 요청 시 예외를 반환한다")
  @Test
  void givenInvalidApiKey_whenRetry_FailedGongsiList_thenThrowsException() throws Exception {
    List<String> gongsiCodes = List.of("000000", "000001");
    String requestJson = om.writeValueAsString(gongsiCodes);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(REQUEST_URL + "/retry-failed-gongsi")
                .param("api-key", "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result -> result.getResolvedException().getMessage().contains("Invalid admin key"));

    verifyNoInteractions(processingFailedGongsiService);
  }

  @Test
  @DisplayName("유효한 API 키로 회사명 자동완성 요청 시 200 OK를 반환한다")
  void givenValidApiKey_whenAutofillCompanyName_thenReturnsOk() throws Exception {
    String startDt = "2021-01-01 00:00:00";
    String endDt = "2021-01-01 23:59:59";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(REQUEST_URL + "/company-name-autofill")
                .param("api-key", "test-api-key")
                .param("startDt", startDt)
                .param("endDt", endDt))
        .andExpect(status().isOk());

    verify(companyNameAutofillGenerator).generate(startDt, endDt);
  }

  @Test
  @DisplayName("잘못된 API 키로 회사명 자동완성 요청 시 예외를 반환한다")
  void givenInvalidApiKey_whenAutofillCompanyName_thenThrowsException() throws Exception {
    String startDt = "2021-01-01 00:00:00";
    String endDt = "2021-01-01 23:59:59";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(REQUEST_URL + "/company-name-autofill")
                .param("api-key", "wrong-key")
                .param("startDt", startDt)
                .param("endDt", endDt))
        .andExpect(status().isInternalServerError())
        .andExpect(
            result -> result.getResolvedException().getMessage().contains("Invalid admin key"));

    verifyNoInteractions(companyNameAutofillGenerator);
  }
}
