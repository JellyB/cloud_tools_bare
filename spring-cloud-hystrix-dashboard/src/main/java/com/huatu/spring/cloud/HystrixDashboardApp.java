package com.huatu.spring.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @author hanchao
 * @date 2017/10/2 10:15
 */
@SpringBootApplication
@EnableHystrixDashboard
//@EnableTurbine
public class HystrixDashboardApp {
    public static void main(String[] args){
        SpringApplication.run(HystrixDashboardApp.class,args);
    }
}
