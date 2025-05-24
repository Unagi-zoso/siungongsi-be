package org.bob.siungongsi.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processing_failed_gongsi")
public class ProcessingFailedGongsiEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(name = "gongsi_code", nullable = false, length = 255)
  private String gongsiCode;

  @Column(name = "company_code", nullable = false, length = 255)
  private String companyCode;

  @Column(name = "gongsi_title", nullable = false, length = 255)
  private String gongsiTitle;

  protected ProcessingFailedGongsiEntity() {}

  public ProcessingFailedGongsiEntity(String gongsiCode, String companyCode, String gongsiTitle) {
    this.gongsiCode = gongsiCode;
    this.companyCode = companyCode;
    this.gongsiTitle = gongsiTitle;
  }

  public Long getId() {
    return id;
  }

  public String getGongsiCode() {
    return gongsiCode;
  }

  public String getCompanyCode() {
    return companyCode;
  }

  public String getGongsiTitle() {
    return gongsiTitle;
  }
}
