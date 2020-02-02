package com.github.prontera.exception;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 用于Hibernate Validator的校验异常
 *
 * @author Zhao Junjian
 */
public class InvalidModelException extends ApplicationException {
    private static final long serialVersionUID = 8096590956382108583L;

    public InvalidModelException(@Nonnull Map<String, String> info) {
        super(info.toString());
    }

}
