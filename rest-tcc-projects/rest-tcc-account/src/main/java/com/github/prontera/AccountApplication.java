package com.github.prontera;

import com.github.prontera.annotation.MyBatisRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Zhao Junjian
 * @date 2020/01/17
 */
@MapperScan(basePackages = "com.github.prontera.persistence", annotationClass = MyBatisRepository.class)
@SpringBootApplication
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

}
