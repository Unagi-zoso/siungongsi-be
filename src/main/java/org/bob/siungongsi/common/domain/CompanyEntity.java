package org.bob.siungongsi.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
public class CompanyEntity extends ModifiableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id; // 기업 ID

  @Column(name = "company_name", length = 50, nullable = false, unique = true)
  private String companyName; // 한글 기업명

  @Column(name = "company_code", length = 12, nullable = false, unique = true)
  private String companyCode; // 공시용 식별자 (OpenDart API 식별용)

  @Column(name = "stock_code", length = 6, nullable = false, unique = true)
  private String stockCode; // 상장 종목 코드

  public CompanyEntity() {}

  public CompanyEntity(String companyName, String companyCode, String stockCode) {
    this.companyName = companyName;
    this.companyCode = companyCode;
    this.stockCode = stockCode;
  }

  public Long getId() {
    return id;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getCompanyCode() {
    return companyCode;
  }

  public String getStockCode() {
    return stockCode;
  }
}
