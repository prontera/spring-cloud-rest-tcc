package com.github.prontera.aspect;

import com.github.prontera.enums.NumericStatusCode;
import com.github.prontera.exception.InvalidModelException;
import com.github.prontera.exception.ResolvableStatusException;
import com.github.prontera.model.response.ResolvableResponse;
import com.github.prontera.product.enums.StatusCode;
import com.github.prontera.util.Responses;
import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

/**
 * @author Zhao Junjian
 * @date 2020/01/17
 */
@Aspect
public class FaultBarrierAspect implements Ordered {

    private static final Logger LOGGER = LogManager.getLogger(FaultBarrierAspect.class);

    private final int order;

    public FaultBarrierAspect(int order) {
        this.order = order;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Around(value = "within(com.github.prontera..*) && (@annotation(com.github.prontera.annotation.FaultBarrier))")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed;
        final Signature signature = joinPoint.getSignature();
        final Class returnType = ((MethodSignature) signature).getReturnType();
        try {
            proceed = joinPoint.proceed();
        } catch (Exception e) {
            if (!ResolvableResponse.class.isAssignableFrom(returnType)) {
                throw new IllegalStateException(e);
            }
            if (e instanceof ResolvableStatusException) {
                final NumericStatusCode statusCode = ((ResolvableStatusException) e).getStatusCode();
                final String injectMessage = ((ResolvableStatusException) e).getInjectMessage();
                final String message = Strings.isNullOrEmpty(injectMessage) ? statusCode.message() : injectMessage;
                proceed = Responses.generate(returnType, statusCode, message);
            } else if (e instanceof InvalidModelException) {
                proceed = Responses.generate(returnType, StatusCode.INVALID_MODEL_FIELDS, e.getMessage());
            } else {
                LOGGER.error("UnknownServerException, method signature '{}', request params '{}'", signature, joinPoint.getArgs(), e);
                final StatusCode unknownException = StatusCode.SERVER_UNKNOWN_ERROR;
                proceed = Responses.generate(returnType, unknownException);
            }
        }
        return proceed;
    }

    @Override
    public int getOrder() {
        return order;
    }

}
