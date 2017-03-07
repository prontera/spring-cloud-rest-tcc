package com.github.prontera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

@EnableTurbine
@EnableHystrixDashboard
@SpringBootApplication
public class HystrixDashboardMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HystrixDashboardMsApplication.class, args);
    }

}
