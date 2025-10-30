package org.bob.siungongsi.api.controller;

import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_ID;
import static org.bob.siungongsi.fixture.GongsiFixture.CUSTOM_PAGE;
import static org.bob.siungongsi.fixture.GongsiFixture.CUSTOM_SIZE;
import static org.bob.siungongsi.fixture.GongsiFixture.DEFAULT_PAGE;
import static org.bob.siungongsi.fixture.GongsiFixture.DEFAULT_SIZE;
import static org.bob.siungongsi.fixture.GongsiFixture.NON_EXISTENT_GONGSI_ID;
import static org.bob.siungongsi.fixture.GongsiFixture.SORT_LATEST;
import static org.bob.siungongsi.fixture.GongsiFixture.SORT_OLDEST;
import static org.bob.siungongsi.fixture.GongsiFixture.SORT_VIEWS;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_GONGSI_ID;
import static org.bob.siungongsi.fixture.GongsiFixture.createDefaultGongsiDetailResponse;
import static org.bob.siungongsi.fixture.GongsiFixture.createEmptyGongsiListResponse;
import static org.bob.siungongsi.fixture.GongsiFixture.createGongsiListResponseWithSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bob.siungongsi.api.config.SecurityConfigForApi;
import org.bob.siungongsi.api.controller.dto.GongsiResponse;
import org.bob.siungongsi.api.service.GongsiService;
import org.bob.siungongsi.api.service.UserService;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.repository.NotificationRepository;
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
@WebMvcTest(GongsiController.class)
class GongsiControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private GongsiService gongsiService;

  @MockitoBean private UserService userService;

  @MockitoBean private NotificationRepository notificationRepository;

  /** getGongsiList */
  @Test
  @DisplayName("공시 목록 조회 시 목록을 반환한다")
  void whenGetGongsiList_thenReturnsGongsiList() throws Exception {
    // given
    GongsiResponse.GongsiListResponse response = createGongsiListResponseWithSize(DEFAULT_SIZE);
    when(gongsiService.getGongsiList(
            isNull(),
            eq(SORT_LATEST),
            eq(false),
            eq(DEFAULT_PAGE),
            eq(DEFAULT_SIZE),
            isNull(),
            isNull()))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/gongsi"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiList").isArray())
        .andExpect(jsonPath("$.data.gongsiList.length()").value(DEFAULT_SIZE))
        .andExpect(jsonPath("$.data.gongsiListSize").value(DEFAULT_SIZE));
  }

  @Test
  @DisplayName("공시 목록이 비어있으면 빈 리스트를 반환한다")
  void whenGetGongsiListWithNoResults_thenReturnsEmptyList() throws Exception {
    // given
    GongsiResponse.GongsiListResponse response = createEmptyGongsiListResponse();
    when(gongsiService.getGongsiList(
            isNull(),
            eq(SORT_LATEST),
            eq(false),
            eq(DEFAULT_PAGE),
            eq(DEFAULT_SIZE),
            isNull(),
            isNull()))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/gongsi"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiList").isArray())
        .andExpect(jsonPath("$.data.gongsiList.length()").value(0))
        .andExpect(jsonPath("$.data.gongsiListSize").value(0));
  }

  @Test
  @DisplayName("회사 ID로 공시 목록을 필터링한다")
  void whenGetGongsiListWithCompanyId_thenReturnsFilteredList() throws Exception {
    // given
    GongsiResponse.GongsiListResponse response = createGongsiListResponseWithSize(3);
    when(gongsiService.getGongsiList(
            eq(TEST_COMPANY_ID),
            eq(SORT_LATEST),
            eq(false),
            eq(DEFAULT_PAGE),
            eq(DEFAULT_SIZE),
            isNull(),
            isNull()))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/gongsi").param("companyId", String.valueOf(TEST_COMPANY_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsiList").isArray());
  }

  @Test
  @DisplayName("정렬 옵션으로 oldest를 지정하면 오래된 순으로 정렬한다")
  void whenGetGongsiListWithSortOldest_thenReturnsSortedList() throws Exception {
    // given
    GongsiResponse.GongsiListResponse response = createGongsiListResponseWithSize(5);
    when(gongsiService.getGongsiList(
            isNull(),
            eq(SORT_OLDEST),
            eq(false),
            eq(DEFAULT_PAGE),
            eq(DEFAULT_SIZE),
            isNull(),
            isNull()))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/gongsi").param("sort", SORT_OLDEST))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("정렬 옵션으로 views를 지정하면 조회수 순으로 정렬한다")
  void whenGetGongsiListWithSortViews_thenReturnsSortedList() throws Exception {
    // given
    GongsiResponse.GongsiListResponse response = createGongsiListResponseWithSize(5);
    when(gongsiService.getGongsiList(
            isNull(),
            eq(SORT_VIEWS),
            eq(false),
            eq(DEFAULT_PAGE),
            eq(DEFAULT_SIZE),
            isNull(),
            isNull()))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/gongsi").param("sort", SORT_VIEWS))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("content를 true로 설정하면 요약 내용이 포함된다")
  void whenGetGongsiListWithContent_thenReturnsListWithContent() throws Exception {
    // given
    GongsiResponse.GongsiListResponse response = createGongsiListResponseWithSize(5);
    when(gongsiService.getGongsiList(
            isNull(),
            eq(SORT_LATEST),
            eq(true),
            eq(DEFAULT_PAGE),
            eq(DEFAULT_SIZE),
            isNull(),
            isNull()))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/gongsi").param("content", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("날짜 범위로 공시 목록을 필터링한다")
  void whenGetGongsiListWithDateRange_thenReturnsFilteredList() throws Exception {
    // given
    String startDate = "2024-01-01";
    String endDate = "2024-12-31";
    GongsiResponse.GongsiListResponse response = createGongsiListResponseWithSize(5);
    when(gongsiService.getGongsiList(
            isNull(),
            eq(SORT_LATEST),
            eq(false),
            eq(DEFAULT_PAGE),
            eq(DEFAULT_SIZE),
            eq(startDate),
            eq(endDate)))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/gongsi").param("startDate", startDate).param("endDate", endDate))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  @Test
  @DisplayName("페이지 번호와 사이즈로 페이지네이션을 적용한다")
  void whenGetGongsiListWithPagination_thenReturnsPaginatedList() throws Exception {
    // given
    GongsiResponse.GongsiListResponse response = createGongsiListResponseWithSize(CUSTOM_SIZE);
    when(gongsiService.getGongsiList(
            isNull(),
            eq(SORT_LATEST),
            eq(false),
            eq(CUSTOM_PAGE),
            eq(CUSTOM_SIZE),
            isNull(),
            isNull()))
        .thenReturn(response);

    // when & then
    mockMvc
        .perform(
            get("/v1/gongsi")
                .param("page", String.valueOf(CUSTOM_PAGE))
                .param("size", String.valueOf(CUSTOM_SIZE)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_LIST_SUCCESS.getCode()));
  }

  /** getGongsiDetail */
  @Test
  @DisplayName("공시 상세 조회 시 공시 정보를 반환한다")
  void whenGetGongsiDetail_thenReturnsGongsiDetail() throws Exception {
    // given
    GongsiResponse.GongsiDetailResponse response = createDefaultGongsiDetailResponse();
    when(gongsiService.getGongsiDetail(eq(TEST_GONGSI_ID), anyString())).thenReturn(response);

    // when & then
    mockMvc
        .perform(get("/v1/gongsi/{gongsiId}", TEST_GONGSI_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_DETAIL_SUCCESS.getCode()))
        .andExpect(jsonPath("$.data.gongsi").exists())
        .andExpect(jsonPath("$.data.company").exists());
  }

  @Test
  @DisplayName("존재하지 않는 공시 ID로 조회 시 404 Not Found를 반환한다")
  void whenGetGongsiDetailWithNonExistentId_thenReturnsNotFound() throws Exception {
    // given
    when(gongsiService.getGongsiDetail(eq(NON_EXISTENT_GONGSI_ID), anyString()))
        .thenThrow(new CustomException(ApiResponseCode.GONGSI_NOT_FOUND));

    // when & then
    mockMvc
        .perform(get("/v1/gongsi/{gongsiId}", NON_EXISTENT_GONGSI_ID))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(ApiResponseCode.GONGSI_NOT_FOUND.getCode()));
  }

  @Test
  @DisplayName("유효하지 않은 공시 ID로 조회 시 400 Bad Request를 반환한다")
  void whenGetGongsiDetailWithInvalidId_thenReturnsBadRequest() throws Exception {
    // when & then
    mockMvc.perform(get("/v1/gongsi/{gongsiId}", -1)).andExpect(status().isBadRequest());
  }
}
