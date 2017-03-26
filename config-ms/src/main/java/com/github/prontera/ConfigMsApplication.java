package com.github.prontera;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringCloudApplication
public class ConfigMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigMsApplication.class, args);
    }
}
