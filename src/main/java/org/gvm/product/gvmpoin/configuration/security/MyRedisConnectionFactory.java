package org.gvm.product.gvmpoin.configuration.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by sofian-hadianto on 5/4/17.
 */
@Configuration
@ComponentScan("org.gvm.product.gvmpoin.module.frauddetection")
public class MyRedisConnectionFactory {

  @Value("${redis.host}")
  private String redisHost;

  @Value("${redis.port}")
  private int redisPort;

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory factory = new JedisConnectionFactory();
    factory.setHostName(redisHost);
    factory.setPort(redisPort);
    factory.setUsePool(true);
    return factory;
  }

  @Bean
  RedisTemplate<String, Object> redisTemplate() {
    final RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
    template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
    return template;
  }

}
