package com.github.prontera;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@MapperScan(basePackages = "com.github.**.persistence", annotationClass = MyBatisRepository.class)
@SpringBootApplication
public class AccountMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountMsApplication.class, args);
    }
}
