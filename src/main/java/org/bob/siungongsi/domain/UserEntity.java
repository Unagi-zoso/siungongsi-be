package org.bob.siungongsi.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "social_id", nullable = false, length = 50, unique = true)
  private String socialId;

  @Column(name = "access_token", nullable = false, length = 50)
  private String accessToken;

  @Column(name = "push_token_id", length = 150)
  private String pushTokenId;

  @Column(name = "noti_flag", nullable = false)
  private Short notiFlag = 0;

  @Column(name = "created_dt", nullable = false)
  private LocalDateTime createdDt;

  @Column(name = "updated_dt", nullable = false)
  private LocalDateTime updatedDt;

  protected UserEntity() {}

  // 필드 값을 받는 생성자 추가
  public UserEntity(String socialId, String accessToken) {
    this.socialId = socialId;
    this.accessToken = accessToken;
  }

  @PrePersist
  public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    this.createdDt = now;
    this.updatedDt = now;
  }

  public Long getId() {
    return id;
  }

  public String getSocialId() {
    return socialId;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getPushTokenId() {
    return pushTokenId;
  }

  public Short getNotiFlag() {
    return notiFlag;
  }

  public LocalDateTime getCreatedDt() {
    return createdDt;
  }

  public LocalDateTime getUpdatedDt() {
    return updatedDt;
  }

  public void updateAccessToken(String accessToken) {
    this.accessToken = accessToken;
    this.updatedDt = LocalDateTime.now();
  }

  public void updatePushTokenId(String pushTokenId) {
    this.pushTokenId = pushTokenId;
    this.updatedDt = LocalDateTime.now();
  }

  public void updateNotiFlag(Short notiFlag) {
    this.notiFlag = notiFlag;
    this.updatedDt = LocalDateTime.now();
  }
}
