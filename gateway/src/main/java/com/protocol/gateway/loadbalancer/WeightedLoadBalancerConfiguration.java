package com.protocol.gateway.loadbalancer;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class WeightedLoadBalancerConfiguration {
  @Bean
  ReactorLoadBalancer<ServiceInstance> createLoadBalancer(Environment environment,
      LoadBalancerClientFactory loadBalancerClientFactory) {
    String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
    return new WeightedLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
        name);
  }
}
