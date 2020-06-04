package org.gvm.product.gvmpoin.module.frauddetection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

/**
 * Created by sofian-hadianto on 5/8/17.
 */
@Repository
public class FraudDataRedisRepository {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private ValueOperations valueOps;

  private ListOperations listOps;

  @PostConstruct
  private void init() {
    valueOps = redisTemplate.opsForValue();
    listOps = redisTemplate.opsForList();
  }

  public void save(String key, int value) {
    valueOps.set(key, value);
  }

  public void save(String key, boolean value) {
    valueOps.set(key, value);
  }

  public void expire(String key, long timeoutInSeconds) {
    redisTemplate.expire(key, timeoutInSeconds, TimeUnit.SECONDS);
  }

  public long increment(String key, long deltaEachIncrement) {
    return valueOps.increment(key, deltaEachIncrement);
  }

  public Object findOneByKey(String key) {
    return valueOps.get(key);
  }

  public long listSize(String key) {
    return listOps.size(key);
  }

  public void saveToList(String key, long value) {
    listOps.rightPush(key, value);
  }

  public List<String> findAllListElement(String key, long start, long end) {
    List<String> elements = listOps.range(key, start, end);

    return elements;
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }
}
