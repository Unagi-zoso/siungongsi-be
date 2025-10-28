package org.bob.siungongsi.api.controller;

import static org.bob.siungongsi.fixture.CompanyFixture.EMPTY_KEYWORD;
import static org.bob.siungongsi.fixture.CompanyFixture.NON_EXISTENT_KEYWORD;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_SEARCH_KEYWORD;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.bob.siungongsi.api.config.SecurityConfigForApi;
import org.bob.siungongsi.api.controller.dto.CompanyResponse.CompanyNameListResponse;
import org.bob.siungongsi.api.controller.dto.CompanyResponse.CompanyNameResponse;
import org.bob.siungongsi.api.service.CompanyService;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.testhelper.config.SecurityConfigBeansForTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Import({SecurityConfigForApi.class, SecurityConfigBeansForTest.class})
@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CompanyService companyService;

  private List<CompanyNameResponse> createCompanyNameResponses(int size) {
    return IntStream.range(0, size)
        .mapToObj(i -> new CompanyNameResponse((long) (i + 1), "테스트기업" + i))
        .toList();
  }

  /** getCompanyNames */
  @Test
  @DisplayName("회사명 검색 시 최대 5개 결과를 반환한다")
  void whenGetCompanyNames_thenReturns5Companies() throws Exception {
    // given
    CompanyNameListResponse response = CompanyNameListResponse.from(createCompanyNameResponses(5));
    when(companyService.getCompanyNames(TEST_SEARCH_KEYWORD)).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/companies/name").param("keyword", TEST_SEARCH_KEYWORD))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code").value(ApiResponseCode.COMPANY_GET_NAME_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.companyNameList").isArray())
        .andExpect(jsonPath("$.data.companyNameList.length()").value(5));
  }

  @Test
  @DisplayName("검색 결과가 없으면 빈 리스트를 반환한다")
  void whenGetCompanyNamesWithNoResults_thenReturnsEmptyList() throws Exception {
    // given
    CompanyNameListResponse response = CompanyNameListResponse.from(Collections.emptyList());
    when(companyService.getCompanyNames(NON_EXISTENT_KEYWORD)).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/companies/name").param("keyword", NON_EXISTENT_KEYWORD))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.code").value(ApiResponseCode.COMPANY_GET_NAME_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.companyNameList").isArray())
        .andExpect(jsonPath("$.data.companyNameList.length()").value(0));
  }

  @Test
  @DisplayName("키워드 길이가 1자 미만이면 400 Bad Request를 반환한다")
  void whenGetCompanyNamesWithTooShortKeyword_thenReturnsBadRequest() throws Exception {
    // when & then
    mockMvc
        .perform(get("/v1/companies/name").param("keyword", EMPTY_KEYWORD))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.code").value(ApiResponseCode.COMPANY_INVALID_KEYWORD_LENGTH.getCode()));
  }

  @Test
  @DisplayName("키워드 길이가 18자 초과이면 400 Bad Request를 반환한다")
  void whenGetCompanyNamesWithTooLongKeyword_thenReturnsBadRequest() throws Exception {
    // given
    String tooLongKeyword = "a".repeat(19);

    // when & then
    mockMvc
        .perform(get("/v1/companies/name").param("keyword", tooLongKeyword))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.code").value(ApiResponseCode.COMPANY_INVALID_KEYWORD_LENGTH.getCode()));
  }
}
