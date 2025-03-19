package org.bob.siungongsi.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "noti_histories")
public class NotiHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "company_id", nullable = false)
  private Long companyId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "created_dt", nullable = false)
  private LocalDateTime createdDt;

  public NotiHistoryEntity(Long userId, Long companyId) {
    this.userId = userId;
    this.companyId = companyId;
    this.createdDt = LocalDateTime.now(); // 현재 시간으로 설정
  }

  public NotiHistoryEntity() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Long companyId) {
    this.companyId = companyId;
  }
}
