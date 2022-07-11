package com.protocol.gateway.loadbalancer;

import com.ecwid.consul.v1.health.model.Check;
import com.ecwid.consul.v1.health.model.Check.CheckStatus;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.consul.discovery.ConsulServiceInstance;
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
    return supplier.get().next().map(this::getInstanceResponse);
  }

  private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
    if (instances.isEmpty()) {
      return new EmptyResponse();
    }

    List<ServiceInstance> healthyInstances = instances.stream().filter((this::isHealthyInstance))
        .collect(Collectors.toList());

    int size = healthyInstances.stream().map(this::getInstanceWeight).reduce(0, Integer::sum);

    int index = ThreadLocalRandom.current().nextInt(size);

    int currentIndex = 0;
    for (ServiceInstance instance : healthyInstances) {
      currentIndex += this.getInstanceWeight(instance);
      if (index < currentIndex) {
        return new DefaultResponse(instance);
      }
    }
    return new DefaultResponse(instances.get(0));
  }

  private boolean isHealthyInstance(ServiceInstance instance) {
    if (instance instanceof ConsulServiceInstance) {
      ConsulServiceInstance consulServiceInstance = (ConsulServiceInstance) instance;
      return !consulServiceInstance.getHealthService()
          .getChecks()
          .stream()
          .anyMatch(check -> !CheckStatus.PASSING.equals(check.getStatus()));
    } else {
      // TODO warning "Not a ConsulServiceInstance"
      return true;
    }
  }

  private int getInstanceWeight(ServiceInstance instance) {
    String weight = instance.getMetadata().get("weight");
    try {
      return Integer.valueOf(weight);
    }
    catch (NumberFormatException ex){
      // TODO warning "Invalid weight {weight}"
      return 1;
    }
  }
}
