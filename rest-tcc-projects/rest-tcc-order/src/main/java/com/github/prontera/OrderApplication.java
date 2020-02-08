package com.github.prontera;

import com.github.prontera.annotation.MyBatisRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.TimeZone;

/**
 * <a href="https://cloud.spring.io/spring-cloud-commons/2.1.x/multi/multi__spring_cloud_commons_common_abstractions.html">discovery client</a>
 *
 * @author Zhao Junjian
 * @date 2020/01/17
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.github.prontera.persistence", annotationClass = MyBatisRepository.class)
public class OrderApplication {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(OrderApplication.class)
            .web(WebApplicationType.REACTIVE)
            .run(args);
    }

}
