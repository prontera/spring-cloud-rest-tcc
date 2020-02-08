package com.github.prontera;

import com.github.prontera.enums.NumericStatusCode;
import com.github.prontera.exception.ResolvableStatusException;

import javax.annotation.Nonnull;

/**
 * @author Zhao Junjian
 * @date 2020/01/22
 */
public final class Shifts {

    private Shifts() {
    }

    public static void fatal(@Nonnull NumericStatusCode element) {
        throw new ResolvableStatusException(element);
    }

}
