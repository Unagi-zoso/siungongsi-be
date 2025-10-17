package org.bob.siungongsi.fixture;

import static org.bob.siungongsi.fixture.AuthFixture.TEST_KAKAO_ACCESS_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_SOCIAL_ID;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.stream.IntStream;

import org.bob.siungongsi.common.domain.UserEntity;

public class UserFixture {

  // 테스트용 사용자 ID 상수
  public static final Long TEST_USER_ID = 1L;
  public static final Long TEST_USER_ID_2 = 2L;

  public static class UserEntityBuilder {
    private String socialId = TEST_SOCIAL_ID;
    private String accessToken = TEST_KAKAO_ACCESS_TOKEN;
    private String pushTokenId = null;
    private boolean notiFlag = false;

    public UserEntityBuilder socialId(String socialId) {
      this.socialId = socialId;
      return this;
    }

    public UserEntityBuilder accessToken(String accessToken) {
      this.accessToken = accessToken;
      return this;
    }

    public UserEntityBuilder pushTokenId(String pushTokenId) {
      this.pushTokenId = pushTokenId;
      return this;
    }

    public UserEntityBuilder notiFlag(boolean notiFlag) {
      this.notiFlag = notiFlag;
      return this;
    }

    public UserEntity build() {
      UserEntity user = new UserEntity(socialId, accessToken);
      if (pushTokenId != null) {
        user.updatePushTokenId(pushTokenId);
      }
      user.updateNotiFlag(notiFlag);
      return user;
    }
  }

  public static UserEntityBuilder userEntity() {
    return new UserEntityBuilder();
  }

  /** Service 단위 테스트용 Mock UserEntity를 생성합니다. Mock 객체를 생성하고 필요한 필드값을 stub하여 반환합니다. */
  public static UserEntity createMockedUser(Long id, String socialId, String accessToken) {
    UserEntity user = mock(UserEntity.class);
    lenient().when(user.getId()).thenReturn(id);
    lenient().when(user.getSocialId()).thenReturn(socialId);
    lenient().when(user.getAccessToken()).thenReturn(accessToken);
    return user;
  }

  public static UserEntity createMockedUser(Long id) {
    return createMockedUser(id, TEST_SOCIAL_ID, TEST_KAKAO_ACCESS_TOKEN);
  }

  // 단건 생성 편의 메서드 (Repository/Integration 테스트용)
  public static UserEntity createDefaultUser() {
    return userEntity().build();
  }

  public static UserEntity createUser(String socialId, String accessToken) {
    return userEntity().socialId(socialId).accessToken(accessToken).build();
  }

  // 다건 생성 편의 메서드
  public static List<UserEntity> createUsers(int size) {
    return IntStream.range(0, size)
        .mapToObj(i -> createUser(TEST_SOCIAL_ID + i, TEST_KAKAO_ACCESS_TOKEN + "-" + i))
        .toList();
  }
}
