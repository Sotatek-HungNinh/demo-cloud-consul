package com.protocol.gateway.loadbalancer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

public class WeightedLoadBalancer implements ReactorServiceInstanceLoadBalancer {
  final String serviceId;

  private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
  public WeightedLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId ) {
    this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    this.serviceId = serviceId;
  }

  @Override
  public Mono<Response<ServiceInstance>> choose(Request request) {
    ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
    return supplier.get().next().map((List<ServiceInstance> serviceInstances) -> getInstanceResponse(serviceInstances));
  }

  private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
    if (instances.isEmpty()) {
      return new EmptyResponse();
    }

    int size = instances.stream()
        .map(instance -> this.getInstanceWeight(instance))
        .reduce(0, (total, weight) -> total + weight);

    int index = ThreadLocalRandom.current().nextInt(size);

    int currentIndex = 0;
    for (ServiceInstance instance : instances) {
      currentIndex += this.getInstanceWeight(instance);
      if (index < currentIndex) {
        return new DefaultResponse(instance);
      }
    }
    return new DefaultResponse(instances.get(0));
  }

  private int getInstanceWeight(ServiceInstance instance) {
    String weight = instance.getMetadata().get("weight");
    try {
      return Integer.valueOf(weight);
    }
    catch (NumberFormatException ex){
      return 1;
    }
  }
}
