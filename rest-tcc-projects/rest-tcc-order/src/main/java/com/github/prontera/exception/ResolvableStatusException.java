package com.github.prontera.exception;

import com.github.prontera.enums.StatusCode;

import java.util.Objects;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
public class ResolvableStatusException extends RuntimeException {

    private static final long serialVersionUID = -7620210836307698986L;

    private final StatusCode statusCode;

    private final String injectMessage;

    public ResolvableStatusException(StatusCode statusCode) {
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = null;
    }

    public ResolvableStatusException(StatusCode statusCode, String injectMessage) {
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = injectMessage;
    }

    public ResolvableStatusException(String message, StatusCode statusCode, String injectMessage) {
        super(message);
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = injectMessage;
    }

    public ResolvableStatusException(String message, Throwable cause, StatusCode statusCode, String injectMessage) {
        super(message, cause);
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = injectMessage;
    }

    public ResolvableStatusException(Throwable cause, StatusCode statusCode, String injectMessage) {
        super(cause);
        this.statusCode = Objects.requireNonNull(statusCode);
        this.injectMessage = injectMessage;
    }

    public StatusCode getStatusCode() {
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
