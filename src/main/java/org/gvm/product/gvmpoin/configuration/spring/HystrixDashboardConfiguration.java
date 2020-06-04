package org.gvm.product.gvmpoin.configuration.spring;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!dev")
@Configuration
@EnableCircuitBreaker
@EnableEurekaClient
public class HystrixDashboardConfiguration {

}
