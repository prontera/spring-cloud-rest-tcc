package com.github.prontera.config;

import com.github.prontera.controller.TccResponseErrorHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Zhao Junjian
 */
@Configuration
public class BeanConfiguration {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate template = new RestTemplate();
        template.setErrorHandler(new TccResponseErrorHandler());
        return template;
    }

}
