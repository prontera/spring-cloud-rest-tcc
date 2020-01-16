package com.github.prontera.util;

import com.google.common.collect.ImmutableMap;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author Zhao Junjian
 */
public final class HibernateValidators {

    private static final Validator VALIDATOR;

    private HibernateValidators() {
    }

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }


    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return VALIDATOR.validate(object, groups);
    }

    /**
     * @throws IllegalArgumentException 当校验有错误的时候抛出异常
     */
    public static <T> void throwsIfInvalid(T object, Class<?>... groups) {
        final Set<ConstraintViolation<T>> constraintViolations = validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            final ImmutableMap.Builder<String, String> errorBuilder = ImmutableMap.builder();
            for (ConstraintViolation<T> violation : constraintViolations) {
                errorBuilder.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new IllegalArgumentException(errorBuilder.build().toString());
        }
    }
}
