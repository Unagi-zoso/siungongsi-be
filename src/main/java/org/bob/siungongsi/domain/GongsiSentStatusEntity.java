package org.bob.siungongsi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "gongsi_sent_status")
public class GongsiSentStatusEntity extends ModifiableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "gongsi_id", nullable = false, unique = true) // 공시 ID, 1:1 관계
  private GongsiEntity gongsi;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private PushStatus status = PushStatus.PENDING;

  protected GongsiSentStatusEntity() {}

  public GongsiSentStatusEntity(GongsiEntity gongsi) {
    this.gongsi = gongsi;
    this.status = PushStatus.PENDING;
  }

  public void markAsSent() {
    this.status = PushStatus.SENT;
  }

  public void markAsFailed() {
    this.status = PushStatus.FAILED;
  }

  public GongsiEntity getGongsi() {
    return gongsi;
  }

  public PushStatus getStatus() {
    return status;
  }
}
