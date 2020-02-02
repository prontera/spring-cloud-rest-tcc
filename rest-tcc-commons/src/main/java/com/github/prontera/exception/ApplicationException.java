package com.github.prontera.exception;

/**
 * @author Zhao Junjian
 * @date 2020/01/29
 */
public class ApplicationException extends RuntimeException {
    private static final long serialVersionUID = 6476602059021589106L;

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    protected ApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
