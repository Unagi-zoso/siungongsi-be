package org.bob.siungongsi.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity extends ModifiableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "social_id", nullable = false, length = 50, unique = true)
  private String socialId;

  @Column(name = "access_token", nullable = false, length = 2048)
  private String accessToken;

  @Column(name = "push_token_id", length = 150)
  private String pushTokenId;

  @Column(name = "noti_flag", nullable = false)
  private Short notiFlag = 0;

  protected UserEntity() {}

  // 필드 값을 받는 생성자 추가
  public UserEntity(String socialId, String accessToken) {
    this.socialId = socialId;
    this.accessToken = accessToken;
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

  public void updateAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void updatePushTokenId(String pushTokenId) {
    this.pushTokenId = pushTokenId;
  }

  public void updateNotiFlag(Short notiFlag) {
    this.notiFlag = notiFlag;
  }
}
