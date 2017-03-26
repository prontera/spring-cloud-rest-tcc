package com.github.prontera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin.server.EnableZipkinServer;

@EnableZipkinServer
@SpringBootApplication
public class ZipkinMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZipkinMsApplication.class, args);
    }
}
