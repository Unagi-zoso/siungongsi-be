package org.bob.siungongsi.domain;

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

  @Column(name = "gongsi_code", nullable = false, unique = true, length = 255)
  private String gongsiCode;

  public ProcessingFailedGongsiEntity() {}

  public ProcessingFailedGongsiEntity(String gongsiCode) {
    this.gongsiCode = gongsiCode;
  }
}
