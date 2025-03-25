package org.bob.siungongsi.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "today_processed_gongsi",
    uniqueConstraints = @UniqueConstraint(columnNames = "gongsi_code"))
public class TodayProcessedGongsiEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "gongsi_code", nullable = false, unique = true)
  private String gongsiCode; // 공시 ID (OpenDart 공시 식별자)

  @Column(name = "processed_at", nullable = false, updatable = false)
  private LocalDateTime processedAt;

  // 기본 생성자 (JPA에서 필수)
  protected TodayProcessedGongsiEntity() {}

  // 생성자
  public TodayProcessedGongsiEntity(String gongsiCode) {
    this.gongsiCode = gongsiCode;
    this.processedAt = LocalDateTime.now(); // 생성 시점 자동 설정
  }

  public Long getId() {
    return id;
  }

  public String getGongsiCode() {
    return gongsiCode;
  }

  public LocalDateTime getProcessedAt() {
    return processedAt;
  }
}
