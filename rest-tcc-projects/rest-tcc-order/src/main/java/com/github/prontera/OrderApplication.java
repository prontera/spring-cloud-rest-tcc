package com.github.prontera;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Zhao Junjian
 * @date 2020/01/17
 */
@SpringBootApplication
@MapperScan(basePackages = "com.github.prontera.persistence", annotationClass = MyBatisRepository.class)
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
