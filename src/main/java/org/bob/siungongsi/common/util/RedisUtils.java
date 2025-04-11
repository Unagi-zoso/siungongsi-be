package org.bob.siungongsi.common.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisUtils {

  private final RedisTemplate<String, Object> redisTemplate;

  public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void set(String key, Object value, Long expiredTime) {
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
}
