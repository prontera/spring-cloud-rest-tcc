package com.github.prontera;

import com.github.prontera.exception.RestStatusException;
import com.github.prontera.model.response.ErrorEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Zhao Junjian
 */
public final class Shift {

    private Shift() {
    }

    /**
     * 抛出具体的{@code RestStatus}异常
     *
     * @param status  自定义异常实体
     * @param details 额外添加至details字段中的任意实体, 最终会被解析成JSON
     */
    public static void fatal(RestStatus status, Object... details) {
        checkNotNull(status);
        final ErrorEntity entity = new ErrorEntity(status);
        // inject details
        if (details.length > 0) {
            Optional.of(details).ifPresent(entity::setDetails);
        }
        // put it into request, details entity by Rest Status's name
        String errorCode = String.valueOf(status.code());
        bindStatusCodesInRequestScope(errorCode, entity);
        throw new RestStatusException(errorCode);
    }

    private static void bindStatusCodesInRequestScope(String key, ErrorEntity entity) {
        checkNotNull(entity);
        checkNotNull(key);
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            ((ServletRequestAttributes) requestAttributes).getRequest().setAttribute(key, entity);
        }
    }
}