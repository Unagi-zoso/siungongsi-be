package org.bob.siungongsi.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  private final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

  @Value("${redis.host}")
  private String host;

  @Value("${redis.port}")
  private int port;

  @Value("${redis.ssl}")
  private boolean useSsl;

  @Bean
  public LettuceConnectionFactory lettuceConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(host);
    config.setPort(port);

    LettuceClientConfiguration clientConfig =
        useSsl
            ? LettuceClientConfiguration.builder()
                .useSsl()
                .build()
            : LettuceClientConfiguration.builder().build();

    try {
      LettuceConnectionFactory connectionFactory =
          new LettuceConnectionFactory(config, clientConfig);
      connectionFactory.afterPropertiesSet();

      try (var connection = connectionFactory.getConnection()) {
        String pingResponse = connection.ping();
        if ("PONG".equals(pingResponse)) {
          logger.info("Redis 연결 성공: {}:{}", host, port);
        } else {
          logger.warn("Redis 연결 실패: PING 응답이 예상과 다릅니다: {}:{}", host, port);
        }
      }

      return connectionFactory;
    } catch (Exception e) {
      logger.error("Redis 연결 실패: {}:{}", host, port, e);
      throw e;
    }
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

    return template;
  }
}
