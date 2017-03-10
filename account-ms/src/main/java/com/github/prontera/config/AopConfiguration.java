package com.github.prontera.config;

import com.github.prontera.common.config.aop.HibernateValidatorAspect;
import com.github.prontera.web.StatusCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhao Junjian
 */
@Configuration
public class AopConfiguration {

    @Bean
    public HibernateValidatorAspect hibernateValidatorAspect() {
        final int order = Byte.MAX_VALUE + 2;
        return new HibernateValidatorAspect(order, StatusCode.INVALID_MODEL_FIELDS);
    }

}
