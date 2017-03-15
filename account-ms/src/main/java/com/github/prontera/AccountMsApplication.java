package com.github.prontera;

import com.github.prontera.common.MyBatisRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan(basePackages = "com.github.**.persistence", annotationClass = MyBatisRepository.class)
@SpringBootApplication
public class AccountMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountMsApplication.class, args);
    }
}
