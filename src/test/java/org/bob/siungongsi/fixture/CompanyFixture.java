package org.bob.siungongsi.fixture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;

import org.bob.siungongsi.common.domain.CompanyEntity;

public class CompanyFixture {

  // Company ID
  public static final Long TEST_COMPANY_ID = 1L;
  public static final Long TEST_COMPANY_ID_2 = 2L;
  public static final Long TEST_NON_EXISTENT_COMPANY_ID = 999L;

  // Company 기본 정보
  public static final String TEST_COMPANY_NAME = "테스트기업A";
  public static final String TEST_COMPANY_NAME_2 = "테스트기업B";
  public static final String TEST_COMPANY_CODE = "00000001";
  public static final String TEST_COMPANY_CODE_2 = "00000002";
  public static final String TEST_STOCK_CODE = "000001";
  public static final String TEST_STOCK_CODE_2 = "000002";

  // 검색 키워드
  public static final String TEST_SEARCH_KEYWORD = "테스트";
  public static final String EMPTY_KEYWORD = "";
  public static final String NON_EXISTENT_KEYWORD = "존재하지않는키워드";

  public static class CompanyEntityBuilder {
    private String companyName = TEST_COMPANY_NAME;
    private String companyCode = TEST_COMPANY_CODE;
    private String stockCode = TEST_STOCK_CODE;

    public CompanyEntityBuilder companyName(String companyName) {
      this.companyName = companyName;
      return this;
    }

    public CompanyEntityBuilder companyCode(String companyCode) {
      this.companyCode = companyCode;
      return this;
    }

    public CompanyEntityBuilder stockCode(String stockCode) {
      this.stockCode = stockCode;
      return this;
    }

    public CompanyEntity build() {
      return new CompanyEntity(companyName, companyCode, stockCode);
    }
  }

  public static CompanyEntityBuilder companyEntity() {
    return new CompanyEntityBuilder();
  }

  /** Service 단위 테스트용 Mock CompanyEntity Builder */
  public static class MockedCompanyEntityBuilder {
    private final CompanyEntity company = mock(CompanyEntity.class);

    public MockedCompanyEntityBuilder withId(Long id) {
      when(company.getId()).thenReturn(id);
      return this;
    }

    public MockedCompanyEntityBuilder withCompanyName(String companyName) {
      when(company.getCompanyName()).thenReturn(companyName);
      return this;
    }

    public MockedCompanyEntityBuilder withCompanyCode(String companyCode) {
      when(company.getCompanyCode()).thenReturn(companyCode);
      return this;
    }

    public MockedCompanyEntityBuilder withStockCode(String stockCode) {
      when(company.getStockCode()).thenReturn(stockCode);
      return this;
    }

    public CompanyEntity build() {
      return company;
    }
  }

  public static MockedCompanyEntityBuilder mockedCompany() {
    return new MockedCompanyEntityBuilder();
  }

  // 단건 생성 편의 메서드
  public static CompanyEntity createCompany(
      String companyName, String companyCode, String stockCode) {
    return companyEntity()
        .companyName(companyName)
        .companyCode(companyCode)
        .stockCode(stockCode)
        .build();
  }

  // 다건 생성 편의 메서드
  public static List<CompanyEntity> createCompanies(int size) {
    return IntStream.range(0, size)
        .mapToObj(i -> createCompany("테스트기업" + i, TEST_COMPANY_CODE + i, String.format("%06d", i)))
        .toList();
  }

  // CompanyNameAutofillProjection.CompanyNameRecord 생성 (테스트용)
  public static List<
          org.bob.siungongsi.common.dto.projection.CompanyNameAutofillProjection.CompanyNameRecord>
      createCompanyNameRecords(int size) {
    return IntStream.range(0, size)
        .mapToObj(
            i ->
                new org.bob.siungongsi.common.dto.projection.CompanyNameAutofillProjection
                    .CompanyNameRecord((long) (i + 1), "테스트기업" + i))
        .toList();
  }

  // Mock CompanyEntity 다건 생성 (Service 테스트용, 가변 리스트)
  public static List<CompanyEntity> createMockedCompanies(int size) {
    return createMockedCompanies(size, 1L);
  }

  public static List<CompanyEntity> createMockedCompanies(int size, long startId) {
    return IntStream.range(0, size)
        .mapToObj(
            i -> mockedCompany().withId(startId + i).withCompanyName("기업" + (startId + i)).build())
        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
  }
}
