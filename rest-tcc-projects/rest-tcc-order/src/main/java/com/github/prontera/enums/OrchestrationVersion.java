package com.github.prontera.enums;

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
public enum OrchestrationVersion {
    /**
     * invalid value
     */
    INVALID(Integer.MAX_VALUE),
    /**
     * 初始版本
     */
    V1(1),
    /** LINE SEPARATOR */
    ;

    private static final Map<Integer, OrchestrationVersion> CACHE;

    private static final DefaultParser PARSER;

    private static final DefaultParser GENTLE_PARSER;

    static {
        final int expectedSize = Capacity.toMapExpectedSize(values().length);
        final Map<Integer, OrchestrationVersion> builder = new HashMap<>(expectedSize);
        for (OrchestrationVersion element : values()) {
            builder.put(element.val(), element);
        }
        CACHE = Collections.unmodifiableMap(builder);
        PARSER = new DefaultParser(true);
        GENTLE_PARSER = new DefaultParser(false);
    }

    private final int val;

    OrchestrationVersion(int val) {
        this.val = val;
    }

    public static OrchestrationVersion parse(int val) {
        return PARSER.apply(val);
    }

    public static OrchestrationVersion parseQuietly(int val) {
        return GENTLE_PARSER.apply(val);
    }

    public static <T> OrchestrationVersion parse(@Nonnull T val, @Nonnull EnumFunction<T, OrchestrationVersion> function) {
        Objects.requireNonNull(val);
        Objects.requireNonNull(function);
        return function.apply(val);
    }

    public int val() {
        return val;
    }

    private static final class DefaultParser implements EnumFunction<Integer, OrchestrationVersion> {
        private final boolean throwsIfInvalid;

        private DefaultParser(boolean throwsIfInvalid) {
            this.throwsIfInvalid = throwsIfInvalid;
        }

        @Override
        public OrchestrationVersion apply(@Nonnull Integer source) {
            Objects.requireNonNull(source);
            OrchestrationVersion element = CACHE.get(source);
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

