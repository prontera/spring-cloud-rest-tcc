package com.github.prontera.exception;

/**
 * @author Solar
 */
public class IllegalValidateException extends Exception {
    private static final long serialVersionUID = 3236537114196270331L;

    public IllegalValidateException() {
        super();
    }

    public IllegalValidateException(String message) {
        super(message);
    }

    public IllegalValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalValidateException(Throwable cause) {
        super(cause);
    }

    protected IllegalValidateException(String message, Throwable cause, boolean enableSuppression,
                                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
