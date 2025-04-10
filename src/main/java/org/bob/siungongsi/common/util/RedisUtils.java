package org.bob.siungongsi.common.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisUtils {

  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisTemplate<String, Object> redistBlackListTemplate;

  public RedisUtils(
      RedisTemplate<String, Object> redisTemplate,
      RedisTemplate<String, Object> redistBlackListTemplate) {
    this.redisTemplate = redisTemplate;
    this.redistBlackListTemplate = redistBlackListTemplate;
  }

  public void set(String key, Object value, long expiredTime) {
    redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
  }

  public String get(String key) {
    return (String) redisTemplate.opsForValue().get(key);
  }

  public boolean delete(String key) {
    return redisTemplate.delete(key);
  }

  public boolean hasKey(String key) {
    return redisTemplate.hasKey(key);
  }

  public void setBlackList(String key, Object value, int expiredTime) {
    redistBlackListTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
  }

  public Object getBlackList(String key) {
    return redistBlackListTemplate.opsForValue().get(key);
  }

  public boolean deleteBlackList(String key) {
    return Boolean.TRUE.equals(redistBlackListTemplate.delete(key));
  }

  public boolean hasKeyBlackList(String key) {
    return Boolean.TRUE.equals(redistBlackListTemplate.hasKey(key));
  }
}
