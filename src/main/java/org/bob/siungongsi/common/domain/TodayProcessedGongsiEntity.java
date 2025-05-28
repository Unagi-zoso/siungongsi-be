package org.bob.siungongsi.common.domain;

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

  protected TodayProcessedGongsiEntity() {}

  public TodayProcessedGongsiEntity(String gongsiCode) {
    this.gongsiCode = gongsiCode;
  }

  public Long getId() {
    return id;
  }

  public String getGongsiCode() {
    return gongsiCode;
  }
}
