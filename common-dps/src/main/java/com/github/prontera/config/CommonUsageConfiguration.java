package com.github.prontera.config;

import com.github.prontera.aspect.DelayReturnAspect;
import com.github.prontera.aspect.HibernateValidatorAspect;
import com.github.prontera.aspect.ManualExceptionAspect;
import com.github.prontera.aspect.RequestIdStuffAspect;
import com.github.prontera.aspect.RequestLoggingAspect;
import com.github.prontera.web.filter.ResettableRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author Zhao Junjian
 */
@Configuration
@EnableConfigurationProperties({DelayProperties.class, ManualExceptionProperties.class})
public class CommonUsageConfiguration {
    @Bean
    public DelayReturnAspect delayReturnAspect(DelayProperties properties) {
        return new DelayReturnAspect(Ordered.LOWEST_PRECEDENCE, properties);
    }

    @Bean
    public ManualExceptionAspect manualExceptionAspect(ManualExceptionProperties properties) {
        return new ManualExceptionAspect(Ordered.LOWEST_PRECEDENCE - 1, properties);
    }

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
