package org.bob.siungongsi.fixture;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.bob.siungongsi.api.controller.dto.CompanyResponse;
import org.bob.siungongsi.api.controller.dto.GongsiResponse;
import org.bob.siungongsi.api.controller.dto.PaginationResponse;
import org.bob.siungongsi.common.domain.CompanyEntity;
import org.bob.siungongsi.common.domain.GongsiEntity;

public class GongsiFixture {

  // Gongsi ID
  public static final Long TEST_GONGSI_ID = 1L;
  public static final Long TEST_GONGSI_ID_2 = 2L;
  public static final Long NON_EXISTENT_GONGSI_ID = 999L;

  // Gongsi 기본 정보
  public static final String TEST_GONGSI_CODE = "20240101000001";
  public static final String TEST_GONGSI_TITLE = "테스트공시제목";
  public static final String TEST_CONTENT_SUMMARY = "테스트 공시 요약 내용입니다.";
  public static final String TEST_ORIGINAL_LINK = "https://example.com/gongsi/20240101000001";
  public static final String TEST_S3_KEY = "gongsi_file/20240101/test.zip";

  // 조회수
  public static final int TEST_VIEW_COUNT = 100;

  // OpenDart API
  public static final String TEST_RECEIPT_NO = "20240101000001";
  public static final String TEST_RECEIPT_TITLE = "테스트 공시 제목";
  public static final String TEST_SUMMARY_TEXT = "테스트 요약 내용";
  public static final String TEST_ORIGINAL_TEXT = "원본 공시 텍스트 내용입니다.";
  public static final byte[] TEST_ZIP_FILE_BYTES = new byte[] {0x50, 0x4B, 0x03, 0x04}; // ZIP 헤더
  public static final int OPENDART_PAGE_SIZE = 100;

  // 페이징
  public static final int DEFAULT_PAGE = 1;
  public static final int DEFAULT_SIZE = 8; // Controller 기본값
  public static final int CUSTOM_PAGE = 2;
  public static final int CUSTOM_SIZE = 10;

  // 정렬 옵션
  public static final String SORT_LATEST = "latest";
  public static final String SORT_OLDEST = "oldest";
  public static final String SORT_VIEWS = "views";
  public static final String SORT_INVALID = "invalid";

  // 날짜 포맷
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String DATE_FORMAT_TIME = "yy.MM.dd hh:mm";
  public static final String INVALID_DATE = "invalid-date";

  // 시간 관련 (테스트용 더미 값)
  public static final LocalDateTime TEST_DEFAULT_DATE_TIME = LocalDateTime.of(2024, 1, 1, 10, 0);
  public static final LocalDate TEST_DEFAULT_DATE = LocalDate.of(2024, 1, 1);
  public static final String TEST_DEFAULT_DATE_STR =
      TEST_DEFAULT_DATE.format(ofPattern(DATE_FORMAT));

  // 네트워크 (조회수 기록용)
  public static final String TEST_IP_ADDRESS = "127.0.0.1";

  // 스케줄러
  public static final long GONGSI_SCHEDULER_INTERVAL = 600000L; // 10분

  /** Service 단위 테스트용 Mock GongsiEntity Builder */
  public static class MockedGongsiEntityBuilder {
    private final GongsiEntity gongsi = mock(GongsiEntity.class);

    public MockedGongsiEntityBuilder withId(Long id) {
      when(gongsi.getId()).thenReturn(id);
      return this;
    }

    public MockedGongsiEntityBuilder withCompany(CompanyEntity company) {
      when(gongsi.getCompany()).thenReturn(company);
      return this;
    }

    public MockedGongsiEntityBuilder withGongsiCode(String gongsiCode) {
      when(gongsi.getGongsiCode()).thenReturn(gongsiCode);
      return this;
    }

    public MockedGongsiEntityBuilder withGongsiTitle(String gongsiTitle) {
      when(gongsi.getGongsiTitle()).thenReturn(gongsiTitle);
      return this;
    }

    public MockedGongsiEntityBuilder withContentSummary(String contentSummary) {
      when(gongsi.getContentSummary()).thenReturn(contentSummary);
      return this;
    }

    public MockedGongsiEntityBuilder withOriginalGongsiLink(String originalGongsiLink) {
      when(gongsi.getOriginalGongsiLink()).thenReturn(originalGongsiLink);
      return this;
    }

    public MockedGongsiEntityBuilder withS3Key(String s3Key) {
      when(gongsi.getOriginalFileS3Key()).thenReturn(s3Key);
      return this;
    }

    public MockedGongsiEntityBuilder withCreatedDt(java.time.LocalDateTime createdDt) {
      when(gongsi.getCreatedDt()).thenReturn(createdDt);
      return this;
    }

    public GongsiEntity build() {
      return gongsi;
    }
  }

  public static MockedGongsiEntityBuilder mockedGongsi() {
    return new MockedGongsiEntityBuilder();
  }

  // Controller 테스트용 Response 생성 헬퍼 메서드

  /** GongsiItem 생성 */
  public static GongsiResponse.GongsiItem createGongsiItem(
      long gongsiId,
      String gongsiTitle,
      String companyName,
      String publishedDatetime,
      int viewCount,
      String content) {
    return GongsiResponse.GongsiItem.of(
        gongsiId, gongsiTitle, companyName, publishedDatetime, viewCount, content);
  }

  public static GongsiResponse.GongsiItem createDefaultGongsiItem() {
    return createGongsiItem(
        TEST_GONGSI_ID,
        TEST_GONGSI_TITLE,
        CompanyFixture.TEST_COMPANY_NAME,
        TEST_DEFAULT_DATE_STR,
        TEST_VIEW_COUNT,
        TEST_CONTENT_SUMMARY);
  }

  /** GongsiItem 리스트 생성 */
  public static List<GongsiResponse.GongsiItem> createGongsiItems(int size) {
    return IntStream.range(0, size)
        .mapToObj(
            i ->
                createGongsiItem(
                    (long) (i + 1),
                    TEST_GONGSI_TITLE + i,
                    CompanyFixture.TEST_COMPANY_NAME,
                    TEST_DEFAULT_DATE_STR,
                    TEST_VIEW_COUNT,
                    TEST_CONTENT_SUMMARY))
        .toList();
  }

  /** PaginationResponse 생성 */
  public static PaginationResponse createPaginationResponse(
      long currentPage, long totalPages, long totalResults) {
    return PaginationResponse.of(currentPage, totalPages, totalResults);
  }

  public static PaginationResponse createDefaultPaginationResponse() {
    return createPaginationResponse(1, 1, 8);
  }

  /** GongsiListResponse 생성 */
  public static GongsiResponse.GongsiListResponse createGongsiListResponse(
      List<GongsiResponse.GongsiItem> gongsiList,
      int gongsiListSize,
      PaginationResponse pagination) {
    return GongsiResponse.GongsiListResponse.of(gongsiList, gongsiListSize, pagination);
  }

  public static GongsiResponse.GongsiListResponse createGongsiListResponseWithSize(int size) {
    List<GongsiResponse.GongsiItem> gongsiList = createGongsiItems(size);
    PaginationResponse pagination = createPaginationResponse(1, 1, size);
    return createGongsiListResponse(gongsiList, size, pagination);
  }

  public static GongsiResponse.GongsiListResponse createEmptyGongsiListResponse() {
    return createGongsiListResponse(Collections.emptyList(), 0, createPaginationResponse(1, 0, 0));
  }

  /** GongsiInfo 생성 */
  public static GongsiResponse.GongsiInfo createGongsiInfo(
      long id, String title, String date, int viewCount, String content, String originalUrl) {
    return GongsiResponse.GongsiInfo.of(id, title, date, viewCount, content, originalUrl);
  }

  public static GongsiResponse.GongsiInfo createDefaultGongsiInfo() {
    return createGongsiInfo(
        TEST_GONGSI_ID,
        TEST_GONGSI_TITLE,
        TEST_DEFAULT_DATE_STR,
        TEST_VIEW_COUNT,
        TEST_CONTENT_SUMMARY,
        TEST_ORIGINAL_LINK);
  }

  /** CompanyInfo 생성 */
  public static CompanyResponse.CompanyInfo createCompanyInfo(
      long id, String name, double prdyCtr, boolean isSubscribed) {
    return CompanyResponse.CompanyInfo.of(id, name, prdyCtr, isSubscribed);
  }

  public static CompanyResponse.CompanyInfo createDefaultCompanyInfo() {
    return createCompanyInfo(
        CompanyFixture.TEST_COMPANY_ID, CompanyFixture.TEST_COMPANY_NAME, 0.0, false);
  }

  /** GongsiDetailResponse 생성 */
  public static GongsiResponse.GongsiDetailResponse createGongsiDetailResponse(
      GongsiResponse.GongsiInfo gongsi, CompanyResponse.CompanyInfo company) {
    return GongsiResponse.GongsiDetailResponse.of(gongsi, company);
  }

  public static GongsiResponse.GongsiDetailResponse createDefaultGongsiDetailResponse() {
    return createGongsiDetailResponse(createDefaultGongsiInfo(), createDefaultCompanyInfo());
  }
}
