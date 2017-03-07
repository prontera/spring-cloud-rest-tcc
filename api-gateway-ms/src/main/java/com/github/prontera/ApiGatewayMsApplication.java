package com.github.prontera;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication
public class ApiGatewayMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayMsApplication.class, args);
    }
}
