package org.bob.siungongsi.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "company_name_autofill")
public class CompanyNameAutofillEntity {

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

  @Column(name = "created_dt", nullable = false)
  private LocalDateTime createdDate;

  public CompanyNameAutofillEntity() {}

  public Long getCompanyId() {
    return companyId;
  }

  public String getCompanyName() {
    return companyName;
  }
}
