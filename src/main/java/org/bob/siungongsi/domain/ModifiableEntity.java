package org.bob.siungongsi.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ModifiableEntity extends BaseTimeEntity {

  @LastModifiedDate
  @Column(name = "updated_dt", nullable = false)
  private LocalDateTime updatedDt;

  public LocalDateTime getUpdatedDt() {
    return updatedDt;
  }
}
