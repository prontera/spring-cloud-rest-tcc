package com.github.prontera.product.enums;

import com.github.prontera.enums.EnumFunction;
import com.github.prontera.util.Capacity;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
public enum ReservingState {
    /**
     * invalid value
     */
    INVALID(Integer.MAX_VALUE),
    /**
     * 处理中 - intermediate state
     */
    TRYING(0),
    /**
     * 交易成功, 依据fsm的设计可以一个虚状态出现, 即可直接删除存储介质上的数据 - final state
     */
    CONFIRMED(1),
    /**
     * 交易超时 - final state
     */
    CANCELLED(2),
    /** LINE SEPARATOR */
    ;

    private static final Map<Integer, ReservingState> CACHE;

    private static final DefaultParser PARSER;

    private static final DefaultParser GENTLE_PARSER;

    static {
        final int expectedSize = Capacity.toMapExpectedSize(values().length);
        final Map<Integer, ReservingState> builder = new HashMap<>(expectedSize);
        for (ReservingState element : values()) {
            builder.put(element.val(), element);
        }
        CACHE = Collections.unmodifiableMap(builder);
        PARSER = new DefaultParser(true);
        GENTLE_PARSER = new DefaultParser(false);
    }

    private final int val;

    ReservingState(int val) {
        this.val = val;
    }

    public static ReservingState parse(int val) {
        return PARSER.apply(val);
    }

    public static ReservingState parseQuietly(int val) {
        return GENTLE_PARSER.apply(val);
    }

    public static <T> ReservingState parse(@Nonnull T val, @Nonnull EnumFunction<T, ReservingState> function) {
        Objects.requireNonNull(val);
        Objects.requireNonNull(function);
        return function.apply(val);
    }

    public int val() {
        return val;
    }

    private static final class DefaultParser implements EnumFunction<Integer, ReservingState> {
        private final boolean throwsIfInvalid;

        private DefaultParser(boolean throwsIfInvalid) {
            this.throwsIfInvalid = throwsIfInvalid;
        }

        @Override
        public ReservingState apply(@Nonnull Integer source) {
            Objects.requireNonNull(source);
            ReservingState element = CACHE.get(source);
            if (element == null) {
                if (throwsIfInvalid) {
                    throw new NoSuchElementException("No matching constant for [" + source + "]");
                } else {
                    element = INVALID;
                }
            }
            return element;
        }
    }

}

