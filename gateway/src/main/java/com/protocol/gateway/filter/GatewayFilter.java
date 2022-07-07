package com.protocol.gateway.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Set;

@Component
public class GatewayFilter implements GlobalFilter {
    Log log = LogFactory.getLog(getClass());
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("START FILTER:");
        Set<URI> originalUris  = exchange.getAttribute("GATEWAY_ORIGINAL_REQUEST_URL_ATTR");
        if (originalUris != null) {
            URI originalUri = originalUris.iterator().next();

            Route route = exchange.getAttribute("GATEWAY_ROUTE_ATTR");

            URI routeUri = exchange.getAttribute("GATEWAY_REQUEST_URL_ATTR");
            log.info("Incoming request " + originalUri.toString() + " is routed to id: " + route.getId()
                    + ", uri:" + routeUri);
        }
        return chain.filter(exchange);
    }
}
