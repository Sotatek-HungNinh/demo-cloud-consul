package com.protocol.gateway.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomRateLimiter extends RedisRateLimiter {
  private ReactiveStringRedisTemplate redisTemplate;
  private RedisScript<List<Long>> script;
  private AtomicBoolean initialized;

  public CustomRateLimiter(int defaultReplenishRate, int defaultBurstCapacity) {
    super(defaultReplenishRate, defaultBurstCapacity);
    this.initialized = new AtomicBoolean(false);
  }

  static List<String> getKeys(String id) {
    String prefix = "request_rate_limiter.{" + id;
    String tokenKey = prefix + "}.tokens";
    String timestampKey = prefix + "}.timestamp";
    return Arrays.asList(tokenKey, timestampKey);
  }

  public void setApplicationContext(ApplicationContext context) throws BeansException {
    super.setApplicationContext(context);
    if (this.initialized.compareAndSet(false, true)) {
      if (this.redisTemplate == null) {
        this.redisTemplate = (ReactiveStringRedisTemplate)context.getBean(ReactiveStringRedisTemplate.class);
      }

      this.script = (RedisScript)context.getBean("redisRequestRateLimiterScript", RedisScript.class);
    }
  }


  public Mono<RateLimiter.Response> isAllowed(String routeId, String id) {
    if (!this.initialized.get()) {
      throw new IllegalStateException("RedisRateLimiter is not initialized");
    } else {
      Config routeConfig = this.loadConfiguration(routeId);
      int replenishRate = routeConfig.getReplenishRate();
      int burstCapacity = routeConfig.getBurstCapacity();
      int requestedTokens = routeConfig.getRequestedTokens();

      try {
        List<String> keys = getKeys(id);
        List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "", "", requestedTokens + "");
        Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys, scriptArgs);
        return flux.onErrorResume((throwable) -> {
//          if (this.log.isDebugEnabled()) {
//            this.log.debug("Error calling rate limiter lua", throwable);
//          }

          return Flux.just(Arrays.asList(1L, -1L));
        }).reduce(new ArrayList(), (longs, l) -> {
          longs.addAll(l);
          return longs;
        }).map((results) -> {
          boolean allowed = (Long)results.get(0) == 1L;
          Long tokensLeft = (Long)results.get(1);
          RateLimiter.Response response = new RateLimiter.Response(allowed, this.getHeaders(routeConfig, tokensLeft));
//          if (this.log.isDebugEnabled()) {
//            this.log.debug("response: " + response);
//          }

          return response;
        });
      } catch (Exception var10) {
//        this.log.error("Error determining if user allowed from redis", var10);
        return Mono.just(new RateLimiter.Response(true, this.getHeaders(routeConfig, -1L)));
      }
    }
  }

  Config loadConfiguration(String routeId) {
    // TODO return config by routeId
    Config config = new Config();
    config.setReplenishRate(10);
    config.setBurstCapacity(1000000);
    config.setRequestedTokens(1);
    return config;
  }
}
