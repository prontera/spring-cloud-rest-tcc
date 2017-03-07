package com.github.prontera;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaRegistryMsApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(EurekaRegistryMsApplication.class).web(true).run(args);
    }
}
