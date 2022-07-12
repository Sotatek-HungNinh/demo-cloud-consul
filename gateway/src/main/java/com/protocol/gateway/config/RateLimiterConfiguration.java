package com.protocol.gateway.config;

import com.protocol.gateway.filter.CustomRateLimiter;
import com.protocol.gateway.filter.RateLimiterKeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfiguration {
  @Bean
  public KeyResolver keyResolver() {
    return new RateLimiterKeyResolver();
  }

  @Bean
  public RateLimiter rateLimiter() {
    return new CustomRateLimiter(10, 10);
  }
}
