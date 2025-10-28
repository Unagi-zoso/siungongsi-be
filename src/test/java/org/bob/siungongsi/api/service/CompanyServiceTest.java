package org.bob.siungongsi.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bob.siungongsi.fixture.CompanyFixture.EMPTY_KEYWORD;
import static org.bob.siungongsi.fixture.CompanyFixture.NON_EXISTENT_KEYWORD;
import static org.bob.siungongsi.fixture.CompanyFixture.TEST_SEARCH_KEYWORD;
import static org.bob.siungongsi.fixture.CompanyFixture.createCompanyNameRecords;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bob.siungongsi.api.controller.dto.CompanyResponse;
import org.bob.siungongsi.common.dto.projection.CompanyNameAutofillProjection;
import org.bob.siungongsi.common.repository.CompanyNameAutofillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

  @InjectMocks private CompanyService companyService;

  @Mock private CompanyNameAutofillRepository companyNameAutofillRepository;

  /** getCompanyNames */
  @Test
  @DisplayName("키워드로 회사명 검색 시 최대 5개 결과를 반환한다")
  void whenGetCompanyNames_thenReturnsTop5Companies() {
    // given
    List<CompanyNameAutofillProjection.CompanyNameRecord> expectedRecords =
        createCompanyNameRecords(5);

    when(companyNameAutofillRepository.findTop5ByKeyword(TEST_SEARCH_KEYWORD))
        .thenReturn(expectedRecords);

    // when
    CompanyResponse.CompanyNameListResponse result =
        companyService.getCompanyNames(TEST_SEARCH_KEYWORD);

    // then
    assertThat(result.companyNameList()).hasSize(5);
    verify(companyNameAutofillRepository, times(1)).findTop5ByKeyword(TEST_SEARCH_KEYWORD);
  }

  @Test
  @DisplayName("검색 결과가 없으면 빈 리스트를 반환한다")
  void whenGetCompanyNamesWithNoResults_thenReturnsEmptyList() {
    // given
    when(companyNameAutofillRepository.findTop5ByKeyword(NON_EXISTENT_KEYWORD))
        .thenReturn(List.of());

    // when
    CompanyResponse.CompanyNameListResponse result =
        companyService.getCompanyNames(NON_EXISTENT_KEYWORD);

    // then
    assertThat(result.companyNameList()).isEmpty();
    verify(companyNameAutofillRepository, times(1)).findTop5ByKeyword(NON_EXISTENT_KEYWORD);
  }

  @Test
  @DisplayName("빈 키워드로 검색 시에도 정상적으로 처리된다")
  void whenGetCompanyNamesWithEmptyKeyword_thenCallsRepository() {
    // given
    when(companyNameAutofillRepository.findTop5ByKeyword(EMPTY_KEYWORD)).thenReturn(List.of());

    // when
    CompanyResponse.CompanyNameListResponse result = companyService.getCompanyNames(EMPTY_KEYWORD);

    // then
    assertThat(result.companyNameList()).isEmpty();
    verify(companyNameAutofillRepository, times(1)).findTop5ByKeyword(anyString());
  }
}
