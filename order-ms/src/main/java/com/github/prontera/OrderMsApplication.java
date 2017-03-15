package com.github.prontera;

import com.github.prontera.common.MyBatisRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//import org.springframework.cloud.client.SpringCloudApplication;

@EnableScheduling
//@SpringCloudApplication
@MapperScan(basePackages = "com.github.**.persistence", annotationClass = MyBatisRepository.class)
@SpringBootApplication
public class OrderMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderMsApplication.class, args);
    }
}
