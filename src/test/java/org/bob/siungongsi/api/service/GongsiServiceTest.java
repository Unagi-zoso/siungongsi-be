package org.bob.siungongsi.api.service;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_ID;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_COMPANY_NAME;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_NON_EXISTENT_COMPANY_ID;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_STOCK_CODE;
import static org.bob.siungongsi.fixture.CompanyFixture.mockedCompany;
import static org.bob.siungongsi.fixture.GongsiFixture.DATE_FORMAT;
import static org.bob.siungongsi.fixture.GongsiFixture.DATE_FORMAT_TIME;
import static org.bob.siungongsi.fixture.GongsiFixture.DEFAULT_PAGE;
import static org.bob.siungongsi.fixture.GongsiFixture.DEFAULT_SIZE;
import static org.bob.siungongsi.fixture.GongsiFixture.INVALID_DATE;
import static org.bob.siungongsi.fixture.GongsiFixture.SORT_INVALID;
import static org.bob.siungongsi.fixture.GongsiFixture.SORT_LATEST;
import static org.bob.siungongsi.fixture.GongsiFixture.SORT_OLDEST;
import static org.bob.siungongsi.fixture.GongsiFixture.SORT_VIEWS;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_CONTENT_SUMMARY;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_DEFAULT_DATE;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_DEFAULT_DATE_STR;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_DEFAULT_DATE_TIME;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_GONGSI_ID;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_GONGSI_ID_2;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_GONGSI_TITLE;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_IP_ADDRESS;
import static org.bob.siungongsi.fixture.GongsiFixture.TEST_ORIGINAL_LINK;
import static org.bob.siungongsi.fixture.GongsiFixture.mockedGongsi;
import static org.bob.siungongsi.fixture.UserFixture.TEST_USER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.bob.siungongsi.api.client.clientinterface.KoreanInvestmentClient;
import org.bob.siungongsi.api.controller.dto.GongsiResponse;
import org.bob.siungongsi.common.domain.CompanyEntity;
import org.bob.siungongsi.common.domain.GongsiEntity;
import org.bob.siungongsi.common.domain.GongsiViewHistoryEntity;
import org.bob.siungongsi.common.dto.ApiResponseCode;
import org.bob.siungongsi.common.exception.CustomException;
import org.bob.siungongsi.common.repository.CompanyRepository;
import org.bob.siungongsi.common.repository.GongsiRepository;
import org.bob.siungongsi.common.repository.GongsiViewHistoryRepository;
import org.bob.siungongsi.common.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class GongsiServiceTest {

  @InjectMocks private GongsiService gongsiService;

  @Mock private GongsiRepository gongsiRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private GongsiViewHistoryRepository gongsiViewHistoryRepository;
  @Mock private NotificationRepository notificationRepository;
  @Mock private KoreanInvestmentClient koreanInvestmentClient;

  private SecurityContext securityContext;
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    securityContext = mock(SecurityContext.class);
    authentication = mock(Authentication.class);
    SecurityContextHolder.setContext(securityContext);
  }

  /** getCompanyIdByGongsiId */
  @Test
  @DisplayName("공시 ID로 기업 ID를 조회한다")
  void whenGetCompanyIdByGongsiId_thenReturnsCompanyId() {
    // given
    CompanyEntity company = mockedCompany().withId(TEST_COMPANY_ID).build();
    GongsiEntity gongsi = mockedGongsi().withCompany(company).build();

    when(gongsiRepository.findById(TEST_GONGSI_ID)).thenReturn(Optional.of(gongsi));

    // when
    Long result = gongsiService.getCompanyIdByGongsiId(TEST_GONGSI_ID);

    // then
    assertThat(result).isEqualTo(TEST_COMPANY_ID);
  }

  @Test
  @DisplayName("존재하지 않는 공시 ID로 조회 시 예외가 발생한다")
  void whenGetCompanyIdByNonExistentGongsiId_thenThrowsException() {
    // given
    when(gongsiRepository.findById(TEST_GONGSI_ID)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> gongsiService.getCompanyIdByGongsiId(TEST_GONGSI_ID))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.GONGSI_NOT_FOUND);
  }

  /** getGongsiList */
  @Test
  @DisplayName("전체 공시 목록을 최신순으로 조회한다")
  void whenGetGongsiListWithLatestSort_thenReturnsGongsiList() {
    // given
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    List<GongsiEntity> gongsiList =
        List.of(
            mockedGongsi()
                .withId(TEST_GONGSI_ID_2)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME.plusDays(1))
                .withCompany(company)
                .build(),
            mockedGongsi()
                .withId(TEST_GONGSI_ID)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .withCompany(company)
                .build());
    Page<GongsiEntity> gongsiPage = new PageImpl<>(gongsiList);
    Pageable pageable =
        PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE, Sort.by(Sort.Direction.DESC, "createdDt"));

    when(gongsiRepository.findByDateRange(TEST_DEFAULT_DATE, TEST_DEFAULT_DATE, pageable))
        .thenReturn(gongsiPage);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(anyLong())).thenReturn(0);

    // when
    GongsiResponse.GongsiListResponse result =
        gongsiService.getGongsiList(
            null,
            SORT_LATEST,
            false,
            DEFAULT_PAGE,
            DEFAULT_SIZE,
            TEST_DEFAULT_DATE_STR,
            TEST_DEFAULT_DATE_STR);

    // then
    assertThat(result.gongsiList()).hasSize(gongsiList.size());
    assertThat(result.gongsiList())
        .isSortedAccordingTo(
            Comparator.comparing(GongsiResponse.GongsiItem::publishedDatetime).reversed());
  }

  @Test
  @DisplayName("전체 공시 목록을 오래된순으로 조회한다")
  void whenGetGongsiListWithOldestSort_thenReturnsGongsiList() {
    // given
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    List<GongsiEntity> gongsiList =
        List.of(
            mockedGongsi()
                .withId(TEST_GONGSI_ID)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .withCompany(company)
                .build(),
            mockedGongsi()
                .withId(TEST_GONGSI_ID_2)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME.plusDays(1))
                .withCompany(company)
                .build());
    Page<GongsiEntity> gongsiPage = new PageImpl<>(gongsiList);
    Pageable pageable =
        PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE, Sort.by(Sort.Direction.ASC, "createdDt"));

    when(gongsiRepository.findByDateRange(TEST_DEFAULT_DATE, TEST_DEFAULT_DATE, pageable))
        .thenReturn(gongsiPage);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(anyLong())).thenReturn(0);

    // when
    GongsiResponse.GongsiListResponse result =
        gongsiService.getGongsiList(
            null,
            SORT_OLDEST,
            false,
            DEFAULT_PAGE,
            DEFAULT_SIZE,
            TEST_DEFAULT_DATE_STR,
            TEST_DEFAULT_DATE_STR);

    // then
    assertThat(result.gongsiList()).hasSize(gongsiList.size());
    assertThat(result.gongsiList())
        .isSortedAccordingTo(Comparator.comparing(GongsiResponse.GongsiItem::publishedDatetime));
  }

  @Test
  @DisplayName("전체 공시 목록을 조회수순으로 조회한다")
  void whenGetGongsiListWithViewsSort_thenReturnsGongsiList() {
    // given
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    List<GongsiEntity> gongsiList =
        List.of(
            mockedGongsi()
                .withId(TEST_GONGSI_ID)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .withCompany(company)
                .build(),
            mockedGongsi()
                .withId(TEST_GONGSI_ID_2)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .withCompany(company)
                .build());
    Page<GongsiEntity> gongsiPage = new PageImpl<>(gongsiList);
    Pageable pageable = PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE);

    when(gongsiRepository.findByDateRangeOrderByViewCount(
            TEST_DEFAULT_DATE, TEST_DEFAULT_DATE, pageable))
        .thenReturn(gongsiPage);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(anyLong())).thenReturn(0);

    // when
    GongsiResponse.GongsiListResponse result =
        gongsiService.getGongsiList(
            null,
            SORT_VIEWS,
            false,
            DEFAULT_PAGE,
            DEFAULT_SIZE,
            TEST_DEFAULT_DATE_STR,
            TEST_DEFAULT_DATE_STR);

    // then
    assertThat(result.gongsiList()).hasSize(gongsiList.size());
    assertThat(result.gongsiList())
        .isSortedAccordingTo(Comparator.comparing(GongsiResponse.GongsiItem::viewCount).reversed());
  }

  @Test
  @DisplayName("기업별 공시 목록을 조회한다")
  void whenGetGongsiListByCompany_thenReturnsGongsiList() {
    // given
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    List<GongsiEntity> gongsiList =
        List.of(
            mockedGongsi()
                .withId(TEST_GONGSI_ID)
                .withCompany(company)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .build(),
            mockedGongsi()
                .withId(TEST_GONGSI_ID + 1)
                .withCompany(company)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .build());
    Page<GongsiEntity> gongsiPage = new PageImpl<>(gongsiList);
    Pageable pageable =
        PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE, Sort.by(Sort.Direction.DESC, "createdDt"));

    when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(company));
    when(gongsiRepository.findByCompanyAndDateRange(
            company, TEST_DEFAULT_DATE, TEST_DEFAULT_DATE, pageable))
        .thenReturn(gongsiPage);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(anyLong())).thenReturn(0);

    // when
    GongsiResponse.GongsiListResponse result =
        gongsiService.getGongsiList(
            TEST_COMPANY_ID,
            SORT_LATEST,
            false,
            DEFAULT_PAGE,
            DEFAULT_SIZE,
            TEST_DEFAULT_DATE_STR,
            TEST_DEFAULT_DATE_STR);

    // then
    assertThat(result.gongsiList()).hasSize(gongsiList.size());
    assertThat(result.gongsiList())
        .allMatch((item) -> item.companyName().equals(TEST_COMPANY_NAME));
  }

  @Test
  @DisplayName("날짜 범위로 공시 목록을 필터링한다")
  void whenGetGongsiListWithDateRange_thenReturnsFilteredList() {
    // given
    LocalDate startDate = TEST_DEFAULT_DATE;
    LocalDate endDate = TEST_DEFAULT_DATE.plusDays(7);
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    List<GongsiEntity> validRangeGongsiList =
        List.of(
            mockedGongsi()
                .withId(TEST_GONGSI_ID)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .withCompany(company)
                .build(),
            mockedGongsi()
                .withId(TEST_GONGSI_ID + 1)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME.plusDays(5))
                .withCompany(company)
                .build());

    Page<GongsiEntity> gongsiPage = new PageImpl<>(validRangeGongsiList);
    Pageable pageable =
        PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE, Sort.by(Sort.Direction.DESC, "createdDt"));

    when(gongsiRepository.findByDateRange(startDate, endDate, pageable)).thenReturn(gongsiPage);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(anyLong())).thenReturn(0);

    // when
    GongsiResponse.GongsiListResponse result =
        gongsiService.getGongsiList(
            null,
            SORT_LATEST,
            false,
            DEFAULT_PAGE,
            DEFAULT_SIZE,
            startDate.format(ofPattern(DATE_FORMAT)),
            endDate.format(ofPattern(DATE_FORMAT)));

    // then
    assertThat(result.gongsiList()).hasSize(validRangeGongsiList.size());
    assertThat(result.gongsiList())
        .allMatch(
            (item) ->
                !LocalDate.parse(
                            item.publishedDatetime(), DateTimeFormatter.ofPattern(DATE_FORMAT_TIME))
                        .isBefore(startDate)
                    && !LocalDate.parse(
                            item.publishedDatetime(), DateTimeFormatter.ofPattern(DATE_FORMAT_TIME))
                        .isAfter(endDate));
  }

  @Test
  @DisplayName("includeContent가 true면 요약 내용을 포함한다")
  void whenGetGongsiListWithIncludeContent_thenReturnsContentSummary() {
    // given
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    List<GongsiEntity> gongsiList =
        List.of(
            mockedGongsi()
                .withId(TEST_GONGSI_ID)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .withCompany(company)
                .withContentSummary(TEST_CONTENT_SUMMARY)
                .build());
    Page<GongsiEntity> gongsiPage = new PageImpl<>(gongsiList);
    Pageable pageable =
        PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE, Sort.by(Sort.Direction.DESC, "createdDt"));

    when(gongsiRepository.findByDateRange(TEST_DEFAULT_DATE, TEST_DEFAULT_DATE, pageable))
        .thenReturn(gongsiPage);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(anyLong())).thenReturn(0);

    // when
    GongsiResponse.GongsiListResponse result =
        gongsiService.getGongsiList(
            null,
            SORT_LATEST,
            true,
            DEFAULT_PAGE,
            DEFAULT_SIZE,
            TEST_DEFAULT_DATE_STR,
            TEST_DEFAULT_DATE_STR);

    // then
    assertThat(result.gongsiList().getFirst().content()).isNotNull();
  }

  @Test
  @DisplayName("includeContent가 false면 요약 내용을 포함하지 않는다")
  void whenGetGongsiListWithoutIncludeContent_thenOmitsContentSummary() {
    // given
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    List<GongsiEntity> gongsiList =
        List.of(
            mockedGongsi()
                .withId(TEST_GONGSI_ID)
                .withCompany(company)
                .withCreatedDt(TEST_DEFAULT_DATE_TIME)
                .build());
    Page<GongsiEntity> gongsiPage = new PageImpl<>(gongsiList);
    Pageable pageable =
        PageRequest.of(DEFAULT_PAGE - 1, DEFAULT_SIZE, Sort.by(Sort.Direction.DESC, "createdDt"));

    when(gongsiRepository.findByDateRange(TEST_DEFAULT_DATE, TEST_DEFAULT_DATE, pageable))
        .thenReturn(gongsiPage);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(anyLong())).thenReturn(0);

    // when
    GongsiResponse.GongsiListResponse result =
        gongsiService.getGongsiList(
            null,
            SORT_LATEST,
            false,
            DEFAULT_PAGE,
            DEFAULT_SIZE,
            TEST_DEFAULT_DATE_STR,
            TEST_DEFAULT_DATE_STR);

    // then
    assertThat(result.gongsiList().getFirst().content()).isNull();
  }

  @Test
  @DisplayName("잘못된 정렬 옵션 사용 시 예외가 발생한다")
  void whenGetGongsiListWithInvalidSort_thenThrowsException() {
    // when & then
    assertThatThrownBy(
            () ->
                gongsiService.getGongsiList(
                    null,
                    SORT_INVALID,
                    false,
                    DEFAULT_PAGE,
                    DEFAULT_SIZE,
                    TEST_DEFAULT_DATE_STR,
                    TEST_DEFAULT_DATE_STR))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.GONGSI_INVALID_SORT_TYPE);
  }

  @Test
  @DisplayName("잘못된 날짜 형식 사용 시 예외가 발생한다")
  void whenGetGongsiListWithInvalidDate_thenThrowsException() {
    // when & then
    assertThatThrownBy(
            () ->
                gongsiService.getGongsiList(
                    null, SORT_LATEST, false, DEFAULT_PAGE, DEFAULT_SIZE, INVALID_DATE, null))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.GONGSI_INVALID_DATE_PAIR);
  }

  @Test
  @DisplayName("존재하지 않는 기업으로 공시 목록 조회 시 예외가 발생한다")
  void whenGetGongsiListWithNonExistentCompany_thenThrowsException() {
    // given
    when(companyRepository.findById(TEST_NON_EXISTENT_COMPANY_ID)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(
            () ->
                gongsiService.getGongsiList(
                    TEST_NON_EXISTENT_COMPANY_ID,
                    SORT_LATEST,
                    false,
                    DEFAULT_PAGE,
                    DEFAULT_SIZE,
                    TEST_DEFAULT_DATE_STR,
                    TEST_DEFAULT_DATE_STR))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.GONGSI_COMPANY_NOT_FOUND);
  }

  /** getGongsiDetail */
  @Test
  @DisplayName("공시 상세를 조회하고 조회수를 기록한다")
  void whenGetGongsiDetail_thenReturnsDetailAndRecordsView() {
    // given
    CompanyEntity company =
        mockedCompany()
            .withId(TEST_COMPANY_ID)
            .withCompanyName(TEST_COMPANY_NAME)
            .withStockCode(TEST_STOCK_CODE)
            .build();
    GongsiEntity gongsi =
        mockedGongsi()
            .withId(TEST_GONGSI_ID)
            .withCompany(company)
            .withGongsiTitle(TEST_GONGSI_TITLE)
            .withContentSummary(TEST_CONTENT_SUMMARY)
            .withOriginalGongsiLink(TEST_ORIGINAL_LINK)
            .withCreatedDt(TEST_DEFAULT_DATE_TIME)
            .build();

    when(gongsiRepository.findById(TEST_GONGSI_ID)).thenReturn(Optional.of(gongsi));
    when(gongsiViewHistoryRepository.existsByGongsiIdAndIpAddress(TEST_GONGSI_ID, TEST_IP_ADDRESS))
        .thenReturn(false);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(TEST_GONGSI_ID)).thenReturn(1);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(authentication.getName()).thenReturn(String.valueOf(TEST_USER_ID));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(notificationRepository.existsByUserIdAndCompanyId(TEST_USER_ID, TEST_COMPANY_ID))
        .thenReturn(false);
    when(koreanInvestmentClient.getPrdyCtr(TEST_STOCK_CODE)).thenReturn(1.5);

    // when
    GongsiResponse.GongsiDetailResponse result =
        gongsiService.getGongsiDetail(TEST_GONGSI_ID, TEST_IP_ADDRESS);

    // then
    assertThat(result.gongsi().id()).isEqualTo(TEST_GONGSI_ID);
    assertThat(result.gongsi().title()).isEqualTo(TEST_GONGSI_TITLE);
    assertThat(result.company().id()).isEqualTo(TEST_COMPANY_ID);
    verify(gongsiViewHistoryRepository, times(1)).save(any(GongsiViewHistoryEntity.class));
  }

  @Test
  @DisplayName("이미 조회한 IP는 조회수를 증가시키지 않는다")
  void whenGetGongsiDetailWithExistingIp_thenDoesNotRecordView() {
    // given
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    GongsiEntity gongsi =
        mockedGongsi()
            .withId(TEST_GONGSI_ID)
            .withCreatedDt(TEST_DEFAULT_DATE_TIME)
            .withCompany(company)
            .build();

    when(gongsiRepository.findById(TEST_GONGSI_ID)).thenReturn(Optional.of(gongsi));
    when(gongsiViewHistoryRepository.existsByGongsiIdAndIpAddress(TEST_GONGSI_ID, TEST_IP_ADDRESS))
        .thenReturn(true);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(TEST_GONGSI_ID)).thenReturn(1);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);

    // when
    GongsiResponse.GongsiDetailResponse result =
        gongsiService.getGongsiDetail(TEST_GONGSI_ID, TEST_IP_ADDRESS);

    // then
    assertThat(result.gongsi().id()).isEqualTo(TEST_GONGSI_ID);
    verify(gongsiViewHistoryRepository, times(0)).save(any(GongsiViewHistoryEntity.class));
  }

  @Test
  @DisplayName("존재하지 않는 공시 상세 조회 시 예외가 발생한다")
  void whenGetGongsiDetailWithNonExistentId_thenThrowsException() {
    // given
    when(gongsiRepository.findById(TEST_GONGSI_ID)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> gongsiService.getGongsiDetail(TEST_GONGSI_ID, TEST_IP_ADDRESS))
        .isInstanceOf(CustomException.class)
        .hasFieldOrPropertyWithValue("errorCode", ApiResponseCode.GONGSI_NOT_FOUND);
  }

  @Test
  @DisplayName("인증된 사용자의 구독 여부를 확인한다")
  void whenGetGongsiDetailWithAuthenticatedUser_thenChecksSubscription() {
    // given
    CompanyEntity company = mockedCompany().withCompanyName(TEST_COMPANY_NAME).build();
    GongsiEntity gongsi =
        mockedGongsi()
            .withId(TEST_GONGSI_ID)
            .withCreatedDt(TEST_DEFAULT_DATE_TIME)
            .withCompany(company)
            .build();

    when(gongsiRepository.findById(TEST_GONGSI_ID)).thenReturn(Optional.of(gongsi));
    when(gongsiViewHistoryRepository.existsByGongsiIdAndIpAddress(anyLong(), anyString()))
        .thenReturn(true);
    when(gongsiViewHistoryRepository.countUniqueViewsByGongsiId(anyLong())).thenReturn(0);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(TEST_USER_ID);
    when(authentication.getName()).thenReturn(String.valueOf(TEST_USER_ID));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(notificationRepository.existsByUserIdAndCompanyId(anyLong(), anyLong())).thenReturn(true);

    // when
    GongsiResponse.GongsiDetailResponse result =
        gongsiService.getGongsiDetail(TEST_GONGSI_ID, TEST_IP_ADDRESS);

    // then
    assertThat(result.company().isSubscribed()).isTrue();
  }
}
