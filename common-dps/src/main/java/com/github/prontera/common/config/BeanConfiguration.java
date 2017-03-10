package com.github.prontera.common.config;

import com.github.prontera.common.config.aop.HibernateValidatorAspect;
import com.github.prontera.common.config.aop.RequestLoggingAspect;
import com.github.prontera.common.config.aop.RequestIdStuffAspect;
import com.github.prontera.common.util.converter.jackson.OffsetDateTimeToIso8601Serializer;
import com.github.prontera.common.web.filter.ResettableRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.OffsetDateTime;

/**
 * @author Zhao Junjian
 */
@Configuration("common-bean")
public class BeanConfiguration {
    @Bean
    public ResettableRequestFilter resettableRequestFilter() {
        return new ResettableRequestFilter();
    }

    @Autowired
    public void objectMapperBuilder(Jackson2ObjectMapperBuilder builder) {
        // ObjectMapper可以将OffsetDateTime转换为ISO8601格式
        builder.serializerByType(OffsetDateTime.class, OffsetDateTimeToIso8601Serializer.INSTANCE);
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
