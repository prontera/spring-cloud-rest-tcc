package com.github.prontera.enums;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Zhao Junjian
 * @date 2020/01/22
 */
public interface NumericStatusCode {

    /**
     * Returns {@code true} if the given element representing successful.
     *
     * @return {@code true} if the given element representing successful.
     */
    static boolean isSuccessful(@Nonnull NumericStatusCode element) {
        Objects.requireNonNull(element);
        return isSuccessful(element.code());
    }

    /**
     * Returns {@code true} if the given element representing successful.
     *
     * @return {@code true} if the given element representing successful.
     */
    static boolean isSuccessful(int code) {
        return code >= 20000 && code <= 30000;
    }

    /**
     * the status codes of per restful request.
     *
     * @return 20xxx if succeed, 40xxx if client error, 50xxx if server side crash.
     */
    int code();

    /**
     * @return status enum name
     */
    String name();

    /**
     * @return message summary
     */
    String message();

}
