package com.github.prontera;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//import org.springframework.cloud.client.SpringCloudApplication;

@EnableAsync
@EnableScheduling
//@SpringCloudApplication
@MapperScan(basePackages = "com.github.**.persistence", annotationClass = MyBatisRepository.class)
@SpringBootApplication
public class OrderMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderMsApplication.class, args);
    }
}
