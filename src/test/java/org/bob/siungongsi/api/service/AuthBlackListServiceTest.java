package org.bob.siungongsi.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_ACCESS_TOKEN;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_BLACKLIST_VALUE;
import static org.bob.siungongsi.fixture.AuthFixture.TEST_TOKEN_EXPIRATION;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bob.siungongsi.common.util.RedisUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthBlackListServiceTest {

  @InjectMocks private AuthBlackListService authBlackListService;

  @Mock private RedisUtils redisUtils;

  /** setBlackList */
  @Test
  @DisplayName("블랙리스트에 토큰을 추가한다")
  void whenSetBlackList_thenSavesToRedis() {
    // when
    authBlackListService.setBlackList(TEST_ACCESS_TOKEN, TEST_BLACKLIST_VALUE, TEST_TOKEN_EXPIRATION);

    // then
    verify(redisUtils, times(1))
        .set(
            eq(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN),
            eq(TEST_BLACKLIST_VALUE),
            eq(TEST_TOKEN_EXPIRATION));
  }

  /** getBlackList */
  @Test
  @DisplayName("블랙리스트에서 토큰 값을 조회한다")
  void whenGetBlackList_thenReturnsValue() {
    // given
    when(redisUtils.get(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN))
        .thenReturn(TEST_BLACKLIST_VALUE);

    // when
    Object result = authBlackListService.getBlackList(TEST_ACCESS_TOKEN);

    // then
    assertThat(result).isEqualTo(TEST_BLACKLIST_VALUE);
    verify(redisUtils, times(1)).get(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN);
  }

  @Test
  @DisplayName("블랙리스트에 없는 토큰 조회 시 null을 반환한다")
  void whenGetBlackListWithNonExistentToken_thenReturnsNull() {
    // given
    when(redisUtils.get(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN)).thenReturn(null);

    // when
    Object result = authBlackListService.getBlackList(TEST_ACCESS_TOKEN);

    // then
    assertThat(result).isNull();
  }

  /** hasKeyBlackList */
  @Test
  @DisplayName("블랙리스트에 토큰이 존재하는지 확인한다")
  void whenHasKeyBlackList_thenReturnsTrue() {
    // given
    when(redisUtils.hasKey(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN)).thenReturn(true);

    // when
    boolean result = authBlackListService.hasKeyBlackList(TEST_ACCESS_TOKEN);

    // then
    assertThat(result).isTrue();
    verify(redisUtils, times(1)).hasKey(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN);
  }

  @Test
  @DisplayName("블랙리스트에 토큰이 없으면 false를 반환한다")
  void whenHasKeyBlackListWithNonExistentToken_thenReturnsFalse() {
    // given
    when(redisUtils.hasKey(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN)).thenReturn(false);

    // when
    boolean result = authBlackListService.hasKeyBlackList(TEST_ACCESS_TOKEN);

    // then
    assertThat(result).isFalse();
  }

  /** deleteBlackList */
  @Test
  @DisplayName("블랙리스트에서 토큰을 삭제한다")
  void whenDeleteBlackList_thenReturnsTrue() {
    // given
    when(redisUtils.delete(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN)).thenReturn(true);

    // when
    boolean result = authBlackListService.deleteBlackList(TEST_ACCESS_TOKEN);

    // then
    assertThat(result).isTrue();
    verify(redisUtils, times(1)).delete(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN);
  }

  @Test
  @DisplayName("블랙리스트에 없는 토큰 삭제 시 false를 반환한다")
  void whenDeleteBlackListWithNonExistentToken_thenReturnsFalse() {
    // given
    when(redisUtils.delete(AuthBlackListService.BLACKLIST_PREFIX + TEST_ACCESS_TOKEN)).thenReturn(false);

    // when
    boolean result = authBlackListService.deleteBlackList(TEST_ACCESS_TOKEN);

    // then
    assertThat(result).isFalse();
  }
}
