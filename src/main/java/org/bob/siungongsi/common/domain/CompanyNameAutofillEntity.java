package org.bob.siungongsi.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_name_autofill")
public class CompanyNameAutofillEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "keyword", nullable = false)
  private String keyword;

  @Column(name = "company_id", nullable = false)
  private Long companyId;

  @Column(name = "company_name", nullable = false)
  private String companyName;

  protected CompanyNameAutofillEntity() {}

  public CompanyNameAutofillEntity(String keyword, Long companyId, String companyName) {
    this.keyword = keyword;
    this.companyId = companyId;
    this.companyName = companyName;
  }

  public Long getCompanyId() {
    return companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getKeyword() {
    return keyword;
  }
}
