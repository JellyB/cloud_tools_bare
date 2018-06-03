package com.huatu.spring.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author zhouwei
 */
@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
@EnableHystrixDashboard
//@EnableCircuitBreaker
public class SpringCloudZuulServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudZuulServerApplication.class, args);
	}
}
