package com.github.prontera.common.config;

import com.github.prontera.common.config.aop.HibernateValidatorAspect;
import com.github.prontera.common.config.aop.RequestIdStuffAspect;
import com.github.prontera.common.config.aop.RequestLoggingAspect;
import com.github.prontera.common.web.filter.ResettableRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhao Junjian
 */
@Configuration("common-bean")
public class BeanConfiguration {
    @Bean
    public ResettableRequestFilter resettableRequestFilter() {
        return new ResettableRequestFilter();
    }

    @Bean
    public RequestIdStuffAspect idStuffAspect() {
        final int order = Byte.MAX_VALUE;
        return new RequestIdStuffAspect(order);
    }

    @Bean
    public RequestLoggingAspect logsAspect() {
        final int order = Byte.MAX_VALUE + 1;
        return new RequestLoggingAspect(order);
    }

    @Bean
    @ConditionalOnMissingBean
    public HibernateValidatorAspect hibernateValidatorAspect() {
        final int order = Byte.MAX_VALUE + 2;
        return new HibernateValidatorAspect(order);
    }
}
