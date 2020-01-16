package com.github.prontera.exception;

/**
 * @author Zhao Junjian
 */
public class RestStatusException extends RuntimeException {
    private static final long serialVersionUID = -8541311111016065562L;

    public RestStatusException(String message) {
        super(message);
    }

    public RestStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestStatusException(Throwable cause) {
        super(cause);
    }

    protected RestStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
