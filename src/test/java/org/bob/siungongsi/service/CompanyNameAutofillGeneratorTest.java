package org.bob.siungongsi.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.bob.siungongsi.domain.CompanyEntity;
import org.bob.siungongsi.domain.CompanyNameAutofillEntity;
import org.bob.siungongsi.repository.CompanyNameAutofillRepository;
import org.bob.siungongsi.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CompanyNameAutofillGeneratorTest {

  private CompanyRepository companyRepository;
  private CompanyNameAutofillRepository companyNameAutofillRepository;
  private CompanyNameAutofillGenerator sut;

  @BeforeEach
  void setUp() {
    companyRepository = mock(CompanyRepository.class);
    companyNameAutofillRepository = mock(CompanyNameAutofillRepository.class);
    sut = new CompanyNameAutofillGenerator(companyRepository, companyNameAutofillRepository);
  }

  @Test
  @DisplayName("유효한 기업들을 입력 받을 때 기업명 자동완성을 생성한다")
  void givenValidCompanies_whenGenerateAutofill_thenCreateAutofill() {
    // given
    String startDt = "2021-01-01 00:00:00";
    String endDt = "2021-01-01 23:59:59";
    List<CompanyEntity> companyNames = List.of(new CompanyEntity("시운", "000001", "000000"));

    when(companyRepository.findByCreatedDtBetween(any(), any())).thenReturn(companyNames);

    // when
    sut.generate(startDt, endDt);

    // then
    ArgumentCaptor<CompanyNameAutofillEntity> captor =
        ArgumentCaptor.forClass(CompanyNameAutofillEntity.class);
    verify(companyNameAutofillRepository, times(5)).save(captor.capture());

    List<String> autofillNames =
        captor.getAllValues().stream().map(CompanyNameAutofillEntity::getKeyword).toList();

    assertAll(
        (() ->
            assertTrue(
                autofillNames.contains("ᄉ"))), // "ᄉ": 유니코드 초성 문자 (U+1109, Hangul Choseong Sios)
        (() -> assertTrue(autofillNames.contains("시"))),
        (() ->
            assertTrue(
                autofillNames.contains("시ᄋ"))), // "ᄋ": 유니코드 종성 문자 (U+110B, Hangul Choseong Ieung)
        (() -> assertTrue(autofillNames.contains("시우"))),
        (() -> assertTrue(autofillNames.contains("시운"))));
  }

  @Test
  @DisplayName("endDt가 없고 유효한 기업들을 입력 받을 때 오늘까지 생성된 기업의 기업명 자동완성을 생성한다")
  void givenValidCompaniesAndNoEndDt_whenGenerateAutofill_thenCreateAutofill() {
    // given
    String startDt = "2021-01-01 00:00:00";
    List<CompanyEntity> companyNames = List.of(new CompanyEntity("시운", "000001", "000000"));

    when(companyRepository.findByCreatedDtBetween(any(), any())).thenReturn(companyNames);

    // when
    sut.generate(startDt, null);

    // then
    ArgumentCaptor<CompanyNameAutofillEntity> captor =
        ArgumentCaptor.forClass(CompanyNameAutofillEntity.class);
    verify(companyNameAutofillRepository, times(5)).save(captor.capture());

    List<String> autofillNames =
        captor.getAllValues().stream().map(CompanyNameAutofillEntity::getKeyword).toList();

    assertAll(
        (() ->
            assertTrue(
                autofillNames.contains("ᄉ"))), // "ᄉ": 유니코드 초성 문자 (U+1109, Hangul Choseong Sios)
        (() -> assertTrue(autofillNames.contains("시"))),
        (() ->
            assertTrue(
                autofillNames.contains("시ᄋ"))), // "ᄋ": 유니코드 종성 문자 (U+110B, Hangul Choseong Ieung)
        (() -> assertTrue(autofillNames.contains("시우"))),
        (() -> assertTrue(autofillNames.contains("시운"))));
  }
}
