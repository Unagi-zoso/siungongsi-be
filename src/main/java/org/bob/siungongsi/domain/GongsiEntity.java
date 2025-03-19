package org.bob.siungongsi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "gongsi",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"gongsi_code"})})
public class GongsiEntity extends ModifiableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) // 연관 관계 설정 (지연 로딩)
  @JoinColumn(name = "company_id", nullable = false)
  private CompanyEntity company;

  @Column(name = "content_summary", columnDefinition = "TEXT", nullable = false)
  private String contentSummary;

  @Column(name = "gongsi_code", unique = true, nullable = false)
  private String gongsiCode;

  @Column(name = "gongsi_title", nullable = false)
  private String gongsiTitle;

  @Column(name = "original_gongsi_link", length = 300, nullable = false)
  private String originalGongsiLink;

  @Column(name = "original_file_s3_key", length = 200, nullable = false)
  private String originalFileS3Key;

  // 기본 생성자 (JPA에서 필수)
  protected GongsiEntity() {}

  // 생성자
  public GongsiEntity(
      CompanyEntity company,
      String contentSummary,
      String gongsiCode,
      String gongsiTitle,
      String originalGongsiLink,
      String originalFileS3Key) {
    this.company = company;
    this.contentSummary = contentSummary;
    this.gongsiCode = gongsiCode;
    this.gongsiTitle = gongsiTitle;
    this.originalGongsiLink = originalGongsiLink;
    this.originalFileS3Key = originalFileS3Key;
  }

  public Long getId() {
    return id;
  }

  public CompanyEntity getCompany() {
    return company;
  }

  public String getContentSummary() {
    return contentSummary;
  }

  public String getGongsiCode() {
    return gongsiCode;
  }

  public String getGongsiTitle() {
    return gongsiTitle;
  }

  public String getOriginalGongsiLink() {
    return originalGongsiLink;
  }

  public String getOriginalFileS3Key() {
    return originalFileS3Key;
  }
}
