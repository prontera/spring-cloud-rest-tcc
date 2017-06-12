package com.github.prontera;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients
@SpringCloudApplication
@MapperScan(basePackages = "com.github.prontera.persistence", annotationClass = MyBatisRepository.class)
public class MembershipMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MembershipMsApplication.class, args);
    }
}
