package com.github.prontera.aspect;

import com.github.prontera.config.DelayProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.concurrent.TimeUnit;

/**
 * @author Zhao Junjian
 */
@Aspect
public class DelayReturnAspect implements Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayReturnAspect.class);
    private final int order;
    private final DelayProperties delayProperties;

    public DelayReturnAspect(int order, DelayProperties delayProperties) {
        this.order = order;
        this.delayProperties = delayProperties;
    }

    @Around("@annotation(com.github.prontera.Delay)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object result = joinPoint.proceed();
        final long timeInMillseconds = delayProperties.getTimeInMillseconds();
        if (timeInMillseconds != 0L) {
            LOGGER.debug("method {} was made delay {} mills to return result", joinPoint.getSignature(), timeInMillseconds);
            TimeUnit.MILLISECONDS.sleep(timeInMillseconds);
        }
        return result;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
