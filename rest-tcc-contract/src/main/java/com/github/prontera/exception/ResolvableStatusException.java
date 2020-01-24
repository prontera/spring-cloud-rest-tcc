package com.github.prontera.exception;

import com.github.prontera.enums.NumericStatusCode;

import java.util.Objects;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
public class ResolvableStatusException extends RuntimeException {

    private static final long serialVersionUID = -7620210836307698986L;

    private final NumericStatusCode statusCode;

    private final String injectMessage;

    public ResolvableStatusException(NumericStatusCode statusCode) {
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = null;
    }

    public ResolvableStatusException(NumericStatusCode statusCode, String injectMessage) {
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = injectMessage;
    }

    public ResolvableStatusException(String message, NumericStatusCode statusCode, String injectMessage) {
        super(message);
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = injectMessage;
    }

    public ResolvableStatusException(String message, Throwable cause, NumericStatusCode statusCode, String injectMessage) {
        super(message, cause);
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = injectMessage;
    }

    public ResolvableStatusException(Throwable cause, NumericStatusCode statusCode, String injectMessage) {
        super(cause);
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = injectMessage;
    }

    public NumericStatusCode getStatusCode() {
        return statusCode;
    }

    public String getInjectMessage() {
        return injectMessage;
    }

    @Override
    public String toString() {
        return "ResolvableStatusException{" +
            "statusCode=" + statusCode +
            ", injectMessage='" + injectMessage + '\'' +
            '}';
    }

}
