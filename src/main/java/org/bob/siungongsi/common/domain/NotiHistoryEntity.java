package org.bob.siungongsi.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "noti_histories")
public class NotiHistoryEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "company_id", nullable = false)
  private Long companyId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  protected NotiHistoryEntity() {}

  public NotiHistoryEntity(Long userId, Long companyId) {
    this.userId = userId;
    this.companyId = companyId;
  }

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

  public Long getUserId() {
    return userId;
  }
}
