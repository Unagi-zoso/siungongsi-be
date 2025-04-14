package org.bob.siungongsi.api.service;

import org.bob.siungongsi.common.util.RedisUtils;
import org.springframework.stereotype.Service;

@Service
public class AuthBlackListService {

  private final RedisUtils redisUtils;
  public static final String BLACKLIST_PREFIX = "blacklist:";

  public AuthBlackListService(RedisUtils redisUtils) {
    this.redisUtils = redisUtils;
  }

  public void setBlackList(String token, Object value, Long expiration) {
    redisUtils.set(BLACKLIST_PREFIX + token, value, expiration);
  }

  public Object getBlackList(String key) {
    return redisUtils.get(BLACKLIST_PREFIX + key);
  }

  public boolean hasKeyBlackList(String token) {
    return redisUtils.hasKey(BLACKLIST_PREFIX + token);
  }

  public boolean deleteBlackList(String key) {
    return redisUtils.delete(BLACKLIST_PREFIX + key);
  }
}
