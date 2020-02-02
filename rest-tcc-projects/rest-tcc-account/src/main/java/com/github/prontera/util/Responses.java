package com.github.prontera.util;

import com.github.prontera.account.enums.StatusCode;
import com.github.prontera.enums.NumericStatusCode;
import com.github.prontera.exception.ResolvableStatusException;
import com.github.prontera.model.response.ResolvableResponse;
import com.google.common.base.Strings;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import reactor.core.Exceptions;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
public final class Responses {

    private static final Objenesis OBJENESIS = new ObjenesisStd(true);

    private Responses() {
    }

    public static <T extends ResolvableResponse> T generate(@Nonnull Class<T> clazz, @Nonnull NumericStatusCode status) {
        return generate(clazz, status, status.message());
    }

    public static <T extends ResolvableResponse> T generate(@Nonnull Class<T> clazz, @Nonnull NumericStatusCode status, @Nonnull Object message) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(status);
        Objects.requireNonNull(message);
        T instance = OBJENESIS.newInstance(clazz);
        instance.setSuccessful(NumericStatusCode.isSuccessful(status));
        instance.setCode(status.code());
        instance.setMessage(message.toString());
        return instance;
    }

    public static <T extends ResolvableResponse> T generate(@Nonnull Class<T> clazz, @Nonnull Throwable throwable) {
        Objects.requireNonNull(throwable);
        Objects.requireNonNull(clazz);
        final Throwable exception = Exceptions.unwrap(throwable);
        final Supplier<T> supplier;
        if (exception instanceof ResolvableStatusException) {
            final NumericStatusCode statusCode = ((ResolvableStatusException) exception).getStatusCode();
            final String injectMessage = ((ResolvableStatusException) exception).getInjectMessage();
            if (Strings.isNullOrEmpty(injectMessage)) {
                supplier = () -> generate(clazz, statusCode);
            } else {
                supplier = () -> generate(clazz, statusCode, injectMessage);
            }
        } else {
            supplier = () -> generate(clazz, StatusCode.SERVER_UNKNOWN_ERROR);
        }
        return supplier.get();
    }

}
