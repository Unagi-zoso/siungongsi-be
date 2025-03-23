package org.bob.siungongsi.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "user_agreed_terms")
public class UserAgreedTermsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "term_id", nullable = false)
  private Long termId;

  @Column(name = "created_dt", nullable = false)
  private LocalDateTime createdDt;

  public UserAgreedTermsEntity() {}

  public UserAgreedTermsEntity(Long userId, Long termId) {
    this.userId = userId;
    this.termId = termId;
    this.createdDt = LocalDateTime.now();
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getTermId() {
    return termId;
  }

  public void setTermId(Long termId) {
    this.termId = termId;
  }

  public LocalDateTime getCreatedDt() {
    return createdDt;
  }

  public void setCreatedDt(LocalDateTime createdDt) {
    this.createdDt = createdDt;
  }
}
