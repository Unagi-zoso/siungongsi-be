package org.bob.siungongsi.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "gongsi_view_histories")
public class GongsiViewHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "gongsi_id", nullable = false)
  private Long gongsiId;

  @Column(name = "ip_address", nullable = false)
  private String ipAddress;

  @Column(name = "createdDt", nullable = false)
  private LocalDateTime createdDt;

  protected GongsiViewHistoryEntity() {}

  public GongsiViewHistoryEntity(Long gongsiId, String ipAddress) {
    this.gongsiId = gongsiId;
    this.ipAddress = ipAddress;
    this.createdDt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public Long getGongsiId() {
    return gongsiId;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public LocalDateTime getCreatedDt() {
    return createdDt;
  }
}
