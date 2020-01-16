package com.github.prontera.aspect;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.prontera.RequestLogging;
import com.github.prontera.RestStatus;
import com.github.prontera.exception.IllegalValidateException;
import com.github.prontera.model.response.ErrorEntity;
import com.github.prontera.web.ServletContextHolder;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author Zhao Junjian
 * @see RequestLogging
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 */
@Aspect
public class HibernateValidatorAspect implements Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateValidatorAspect.class);
    private final int order;
    private final RestStatus throwIfInvalidModel;

    public HibernateValidatorAspect() {
        this(Byte.MAX_VALUE);
    }

    public HibernateValidatorAspect(int order) {
        this(order, DefaultInvalidModelStatus.INVALID_MODEL_STATUS);
    }

    public HibernateValidatorAspect(int order, RestStatus throwIfInvalidModel) {
        this.order = order;
        this.throwIfInvalidModel = throwIfInvalidModel;
    }

    @Around(value = "within(com.github.prontera..*) " +
            "&& (@annotation(org.springframework.web.bind.annotation.ResponseBody)" +
            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping))")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof BindingResult) {
                throwIfInvalidModel((BindingResult) arg, throwIfInvalidModel);
            }
        }
        return joinPoint.proceed();
    }

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * 校验实体合法性, 自动向Map封装错误信息.
     *
     * @param result Spring MVC中与@Valid成对出现的BindingResult, 用于绑定错误信息
     * @throws IllegalValidateException 实体校验失败异常
     * @see org.springframework.web.bind.annotation.ControllerAdvice
     */
    public static void throwIfInvalidModel(BindingResult result, RestStatus errorStatus) {
        Preconditions.checkNotNull(result);
        // 默认为true, 检测到错误时赋值为false
        boolean isValid = true;
        final HashMap<Object, Object> errorMap = Maps.newHashMap();
        if (result.getErrorCount() > 0) {
            isValid = false;
            String errorFieldName;
            for (FieldError fieldError : result.getFieldErrors()) {
                errorFieldName = acquireFieldName(result, fieldError);
                final String errorMessage = fieldError.getDefaultMessage();
                LOGGER.debug("request id: {}, error field: {}, error msg: {}",
                        ServletContextHolder.fetchRequestId(), errorFieldName, errorMessage);
                errorMap.put(errorFieldName, errorMessage);
            }
        }
        if (!isValid) {
            final ErrorEntity entity = new ErrorEntity(errorStatus, errorMap);
            // 以entity中的code为key存入Request中
            final String errorCode = String.valueOf(errorStatus.code());
            bindStatusCodesInRequestScope(errorCode, entity);
            throw new IllegalValidateException(errorCode);
        }
    }

    /**
     * 获取错误的字段名, 如果被{@link JsonProperty}修饰则优先选择
     *
     * @see JsonProperty
     */
    private static String acquireFieldName(BindingResult result, FieldError fieldError) {
        Preconditions.checkNotNull(result);
        Preconditions.checkNotNull(fieldError);
        // 获取错误字段名
        String errorFieldName = fieldError.getField();
        // 获取校验非法的类
        Class<?> clazz = result.getTarget().getClass();
        final Field field;
        try {
            // 获取其字段名
            field = clazz.getDeclaredField(fieldError.getField());
            final JsonProperty annotation = field.getAnnotation(JsonProperty.class);
            // 若JsonProperty里value()不为null则覆盖该值
            if (annotation != null) {
                errorFieldName = annotation.value();
            }
        } catch (NoSuchFieldException e) {
            Throwables.throwIfUnchecked(e);
            LOGGER.error("request id: {}, 反射字段名时抛出异常: {}", ServletContextHolder.fetchRequestId(), e.getMessage());
        }
        return errorFieldName;
    }

    private static void bindStatusCodesInRequestScope(String key, ErrorEntity entity) {
        Preconditions.checkNotNull(entity);
        Preconditions.checkNotNull(key);
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            ((ServletRequestAttributes) requestAttributes).getRequest().setAttribute(key, entity);
        }
    }

    private enum DefaultInvalidModelStatus implements RestStatus {
        INVALID_MODEL_STATUS(40001, "invalid request model");

        DefaultInvalidModelStatus(int code, String messge) {
            this.code = code;
            this.message = messge;
        }

        private final int code;

        private final String message;

        @Override
        public int code() {
            return code;
        }

        @Override
        public String message() {
            return message;
        }
    }
}
