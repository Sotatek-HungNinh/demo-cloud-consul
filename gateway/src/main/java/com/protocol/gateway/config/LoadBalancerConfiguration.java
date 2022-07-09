package com.protocol.gateway.config;

import com.protocol.gateway.loadbalancer.WeightedLoadBalancerConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
@LoadBalancerClients(defaultConfiguration = WeightedLoadBalancerConfiguration.class)
public class LoadBalancerConfiguration {
  @Bean
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }
}
