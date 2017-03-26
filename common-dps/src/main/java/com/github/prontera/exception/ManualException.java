package com.github.prontera.exception;

/**
 * @author Zhao Junjian
 */
public class ManualException extends RuntimeException {
    private static final long serialVersionUID = 8009088829199901509L;

    public ManualException() {
        super();
    }

    public ManualException(String message) {
        super(message);
    }

    public ManualException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManualException(Throwable cause) {
        super(cause);
    }

    protected ManualException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
