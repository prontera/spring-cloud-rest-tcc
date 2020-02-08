package com.github.prontera.util;

import com.github.prontera.exception.InvalidModelException;
import org.hibernate.validator.HibernateValidator;

import javax.annotation.Nonnull;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
public final class HibernateValidators {

    private static final Validator VALIDATOR;

    static {
        ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    private HibernateValidators() {
    }

    /**
     * 当校验有错误的时候聚合成Map集合, 否则返回空集合
     */
    public static <T> Map<String, String> asInvalidMap(@Nonnull T object, Class<?>... groups) {
        Objects.requireNonNull(object);
        final Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(object, groups);
        Map<String, String> errorMap = Collections.emptyMap();
        if (!constraintViolations.isEmpty()) {
            errorMap = new HashMap<>(Capacity.toMapExpectedSize(constraintViolations.size()));
            for (ConstraintViolation<T> violation : constraintViolations) {
                errorMap.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
        return errorMap;
    }

    /**
     * @throws InvalidModelException 当校验有错误的时候抛出异常
     */
    public static <T> void throwsIfInvalid(@Nonnull T object, Class<?>... groups) {
        final Map<String, String> invalidMap = asInvalidMap(object, groups);
        if (!invalidMap.isEmpty()) {
            throw new InvalidModelException(invalidMap);
        }
    }

}
