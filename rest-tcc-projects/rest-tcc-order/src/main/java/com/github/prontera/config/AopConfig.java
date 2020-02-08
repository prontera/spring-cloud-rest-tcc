package com.github.prontera.config;

import com.github.prontera.aspect.FaultBarrierAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zhao Junjian
 * @date 2020/01/17
 */
@Configuration
public class AopConfig {
    @Bean
    public FaultBarrierAspect faultBarrier() {
        return new FaultBarrierAspect(Byte.MAX_VALUE);
    }
}
