package com.protocol.gateway.filter;

import java.security.Principal;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RateLimiterKeyResolver implements KeyResolver {

  @Override
  public Mono<String> resolve(ServerWebExchange exchange) {
    // TODO return key = service + ip
    return Mono.just(exchange.getRequest().getPath().value());
//    ServerHttpRequest request = exchange.getRequest();
//    String address = request.getRemoteAddress().getAddress().toString();
//    Principal principal = exchange.getPrincipal().block();
//    System.out.println(address);
//    System.out.println(principal.getName());
//    return exchange.getPrincipal().flatMap(p -> Mono.justOrEmpty(p.getName()));
  }
}
