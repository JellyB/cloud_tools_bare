package com.huatu.spring.cloud;

import com.alibaba.dcm.DnsCacheManipulator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author zhouwei
 */
@EnableEurekaServer
@SpringBootApplication
public class SpringCloudEurekaServerApplication {

	public static void main(String[] args) {
		DnsCacheManipulator.loadDnsCacheConfig();

		SpringApplication.run(SpringCloudEurekaServerApplication.class, args);
	}
}
